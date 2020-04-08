import markovify
import sqlite3

import json;
import re;

import os;

class Quote():
    def __init__(self,quote,quotee,timestamp,sender,og):
        self.quote = quote
        self.quotee = quotee
        self.timestamp = timestamp
        self.sender = sender
        self.original = og

def loadjson(filename):
    f = open(filename, "r")
    string = f.read()
    f.close()

    string = string.replace("”",'"')

    obj = json.loads(string)
    l = list(map(lambda i: (i["content"].encode("latin-1").decode("UTF-8").replace("”",'"'),i["timestamp_ms"],i["sender_name"]),filter(lambda i: "content" in i, obj["messages"])))

    out = []

    for q in l:
        text = q[0].split('\n')
        for r in text:
            out.append((r,q[1],q[2]))

    return out

def getQuotes(raw):
    c1 = re.compile("^(.+)\\s*-\\s*(.+)$")
    c2 = re.compile("^(\".+\")\\s*(.+)$")
    c3 = re.compile("^(.+)\\s*:\\s*(\".+\")$")
    c4 = re.compile("^((?:\"|”).+(?:\"|”))$")
    qc = re.compile("^[-_'']\\s*(.+)$")
    fbm = re.compile(   "(?:^[^\"]+\\s(?:(?:set your nickname to)|(?:set the nickname for .*? to)|(?:set her own nickname to)|(?:set his own nickname to))\\s.*$)|" +
                        "(?:^.* (?:(?:named)|(?:created)) the group.*$)|" +
                        "(?:^.* changed the (?:(?:group photo)|(?:chat theme)).$)|" +
                        "(?:^.* added .* to the group.$)|" +
                        "(?:^.* set the emoji to .\\.$)|" +
                        "(?:^.* turned on member approval and will review requests to join the group.$)|" +
                        "(?:^.* joined the call.$)|" +
                        "(?:^.* started a call.$)|" +
                        "(?:^The call ended.$)|" +
                        "(?:^The video chat ended.$)|" +
                        "(?:^.* started sharing video.$)|" +
                        "(?:^.$)|" +
                        "(?:^$)")
    quotes = []

    unused = []
    os.makedirs("logs",exist_ok=True)
    f1 = open("logs/raw.txt","w")
    f2 = open("logs/filtered.txt","w")
    f3 = open("logs/quotes.txt","w")
    f4 = open("logs/unused.txt","w")

    for (i,m) in enumerate(raw):
        f1.write(m[0] + "\n")
        if re.match(fbm,m[0]) != None:
            f2.write(m[0] + "\n")
            continue

        matched = False
        pps = m[0].strip()
        res = re.search(c1,pps)
        if res != None:
            quote = normalizeQuote(res.group(1))
            quotee = res.group(2)
            quotes.append(Quote(quote,quotee,m[1],m[2],pps))
            matched = True

        if not matched:
            res = re.search(c2,pps)
            if res != None:
                quote = normalizeQuote(res.group(1))
                quotee = res.group(2)
                quotes.append(Quote(quote,quotee,m[1],m[2],pps))
                matched = True

        if not matched:
            res = re.search(c3,pps)
            if res != None:
                quote = normalizeQuote(res.group(2))
                quotee = res.group(1)
                quotes.append(Quote(quote,quotee,m[1],m[2],pps))
                matched = True

        if not matched:  
            if (i != 0):
                inter = re.search(qc,raw[i-1][0])
                if inter != None:
                    quote = normalizeQuote(pps)
                    quotee = inter.group(1)
                    quotes.append(Quote(quote,quotee,m[1],m[2],pps))
                    matched = True
                    unused.pop()

        if not matched:
            res = re.search(c4,pps)
            if res != None:
                quote = normalizeQuote(res.group(1))
                quotee = "Unknown"
                quotes.append(Quote(quote,quotee,m[1],m[2],pps))
                matched = True
        
        if not matched:
            unused.append(m[0])
    
    for d in unused:
        f4.write(d + "\n")
    for q in quotes:
        f3.write(q.quote + " - " + q.quotee + "\n")
    return (quotes,unused)

def normalizeQuote(quote):
    sq = quote.strip()
    string = '"'
    if quote.startswith('"'):
        string += sq[1:].lstrip()
    else:
        string += sq

    if not sq.endswith('"') or sq[1:].count('"') % 2 == 0:
        string += '"'

    return string

def storeQuotes(quotes):
    conn = sqlite3.connect("markus.db")

    conn.execute('''CREATE TABLE IF NOT EXISTS quotes 
                        (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        quote CHAR(255) NOT NULL,
                        quotee CHAR(127) NOT NULL,
                        timestamp CHAR(15) UNIQUE NOT NULL,
                        sender CHAR(32))
    ''')

    for q in quotes:
        conn.execute('''INSERT INTO quotes(quote,quotee,timestamp,sender) VALUES (?,?,?,?) ON CONFLICT(timestamp) DO UPDATE SET quote = ?, quotee = ?''',
            (q.quote,q.quotee,q.timestamp,q.sender,q.quote,q.quotee))

    conn.commit()
    conn.close()

def storeGenQuotes(gq):
    conn = sqlite3.connect("markus.db")

    conn.execute('''CREATE TABLE IF NOT EXISTS genquotes 
                        (ID INTEGER PRIMARY KEY NOT NULL,
                        quote CHAR(255) NOT NULL UNIQUE)
    ''')

    for (i,q) in enumerate(gq):
        conn.execute('''INSERT OR REPLACE INTO genquotes(ID,quote) SELECT ?, ? WHERE NOT EXISTS(SELECT 1 FROM quotes where quotes.quote=?)''',
            (i,q,q))

    conn.commit()
    conn.close()

s = loadjson("messages.json")
(q,u) = getQuotes(s)
storeQuotes(q)
string = ""
for quote in q:
    string += quote.quote.strip('"') + "\n"

text_model = markovify.NewlineText(string,well_formed=False)

gen_quotes=[]
for i in range(1000):
    quote = text_model.make_sentence()
    if quote != None and not quote in gen_quotes:
        gen_quotes.append('"' + quote + '"')

storeGenQuotes(gen_quotes)
print("Quotes parsed:\t\t{}".format(len(q)))
print("Quotes generated:\t{}".format(len(gen_quotes)))

