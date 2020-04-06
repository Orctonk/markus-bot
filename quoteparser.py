# -*- coding: utf-8 -*-
import sqlite3;
import json;
import re;

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

    obj = json.loads(string)
    l = list(map(lambda i: (i["content"].encode("latin-1").decode("UTF-8"),i["timestamp_ms"],i["sender_name"]),filter(lambda i: "content" in i, obj["messages"])))

    return l

def getQuotes(raw):
    c1 = re.compile("^(.+)\\s*-\\s*(.+)")
    c2 = re.compile("^(\".+\")$")
    qc = re.compile("^-\s*(.+)$")
    quotes = []

    for (i,m) in enumerate(raw):
        matched = False
        pps = m[0].replace(u"”","").strip()
        res = re.search(c1,pps)
        if res != None:
            quote = normalizeQuote(res.group(1))
            quotee = res.group(2)
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

        if not matched:
            res = re.search(c2,pps)
            if res != None:
                quote = normalizeQuote(res.group(1))
                quotee = "Unknown"
                quotes.append(Quote(quote,quotee,m[1],m[2],pps))
                matched = True
    
    return quotes

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

s = loadjson("messages.json")
q = getQuotes(s)
storeQuotes(q)