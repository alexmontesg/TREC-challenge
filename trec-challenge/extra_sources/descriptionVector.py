'''
Created on Jul 28, 2014

@author: Alejandro Montes Garcia
@author: Julia Kiseleva
@license: GPL v2
@organization: Eindhoven University of Technology
'''

import re
import string
import nltk
from nltk.corpus import stopwords
import MySQLdb
import sys

cachedStopWords = stopwords.words("english")
extraStopWords = ["chicago", "chicagos", "1930", "1987", "illinois", "states", "united", "michigan", "located", "side", "north", "east", "west", "south", "center"]
exclude = set(string.punctuation)
class_1 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = -1"
class_2 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = 0"
class_3 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = 1"
class_4 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = 2"
class_5 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = 3"
class_6 = "SELECT DISTINCT(description) FROM training_features_basic WHERE Description_Rating = 4"
venues = "SELECT id, description FROM venues WHERE description IS NOT NULL"

def getWords(classNumber, con):
    words = []
    cur = con.cursor()
    cur.execute(classNumber)
    rows = cur.fetchall()
    for row in rows:
        row = row[0]
        row = row.split("\tALBERT HEIJN\t")
        for description in row :
            description = ''.join(ch for ch in description if ch not in exclude)
            for word in description.lower().split():
                if word not in cachedStopWords and len(word) > 2 and word not in extraStopWords:
                    words.append(word)
    return words
                    
try:
    topWordsClass = []
    con = MySQLdb.Connection('131.155.69.14', 'alejandro', 'alejandro', 'trec_ca_2014')
    
    topWords = nltk.FreqDist(getWords(class_1, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
        
    topWords = nltk.FreqDist(getWords(class_2, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
        
    topWords = nltk.FreqDist(getWords(class_3, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
        
    topWords = nltk.FreqDist(getWords(class_4, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
        
    topWords = nltk.FreqDist(getWords(class_5, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
        
    topWords = nltk.FreqDist(getWords(class_6, con)).keys()[:10]
    for i in range(10):
        topWordsClass.append(topWords[i])
    
    cur = con.cursor()
    cur.execute(venues)
    rows = cur.fetchall()
    for row in rows:
        id = row[0]
        description = row[1].lower()
        vector="("
        for i in range(60):
            if topWordsClass[i] in description:
                vector += "1"
            else :
                vector += "0"
            if i < 59 :
                vector += ", "
        vector += ")"
        insCur = con.cursor()
        insCur.execute("INSERT INTO desc_vector VALUES (" + str(id) + ", '" + vector + "')")
        con.commit()
except MySQLdb.Error, e:
    print "Error %d: %s" % (e.args[0], e.args[1])
    sys.exit(1)
finally:
    if con:
        con.close()