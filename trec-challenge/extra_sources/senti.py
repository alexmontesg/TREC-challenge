from textblob import TextBlob
import csv

text = "this is not a good experience"

def senti(text):
	someblob = TextBlob(text)
	return someblob.sentiment.polarity

#load the example csv file
with open('examples2014.csv', 'rb') as f:
	reader = csv.reader(f)
	for row in reader:
		if(row[0]=='id'):
			print(row[0]+",sentiment")
		else:
			print(row[0]+','+str(senti(row[2])))
