
/*
CREATE TABLE joinedVenues AS
SELECT t1.*,
t2.foursquare_id, t2.facebook_id, t2.google_id, t2.google_reference, t2.yelp_id,
t2.description AS SN_description, t2.score, t2.facebook_likes,
SUBSTRING_INDEX(t1.Url, '/', 3) AS url1,
SUBSTRING_INDEX(t2.url, '/', 3) AS url2
FROM profiles2venues t1 INNER JOIN venues t2 
ON (SUBSTRING_INDEX(SUBSTRING_INDEX(t1.Url, '/', 3), '.', 2) = SUBSTRING_INDEX(SUBSTRING_INDEX(t2.url, '/', 3), '.', 2));
*/

CREATE table training_Profiles2Examples AS
SELECT 
t1.Website_Rating, 
t1.Profile_Id, 
t1.Attraction_Id, 
t2.Title, 
t2.Description, 
t1.Description_Rating, 
t2.Url
FROM profiles t1 INNER JOIN examples t2 
ON (t1.Attraction_Id = t2.Attraction_Id);

CREATE TABLE training_features_basic AS
SELECT t1.*, 
t2.name,
t2.id,
t2.foursquare_score,
t2.yelp_score,
t2.facebook_likes,
t2.distance,
t2.lat,
t2.lng,
t2.description AS SN_description, 
t2.url as SN_url,
t2.foursquare_id,
t2.facebook_id,
t2.yelp_id
FROM trainingProfiles2Examples t1 INNER JOIN trainingVenues t2 
ON t2.name like t1.Title;

select place_id, count(place_id) AS _count FROM categories group by place_id order by _count ;
/*
result:
|     9859 |      8 |
|    64233 |      8 |
|    65736 |      8 |
|    15416 |      9 |
|    12852 |      9 |
|    67738 |     12 |
*/

##Feature Category

create table listCategories (id MEDIUMINT NOT NULL AUTO_INCREMENT, category VARCHAR (256));

INSERT INTO listCategories (category)  
SELECT DISTINCT t.category FROM 
(SELECT category FROM categories 
 UNION ALL 
 SELECT category FROM trainingCategories) t;



create table training_finalCategorises AS 
SELECT t1.place_id, t2.id AS category_id, t1.category 
FROM training_categories t1 INNER JOIN listCategories t2 
ON (t1.category = t2.category);

create table finalCategorises AS  
SELECT t1.place_id, t2.id AS category_id, t1.category  
FROM categories t1 LEFT  JOIN listCategories t2  ON (t1.category = t2.category);


/*Ouput for text classification*/
SELECT id, Website_Rating, Description INTO OUTFILE '/tmp/description_classification_url_raiting.txt' 
FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'  from training_features_basic WHERE description !='';

SELECT id, Description_Rating, Description INTO OUTFILE '/tmp/description_classification_desc_raiting.txt' 
FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'  from training_features_basic WHERE description !='';

SELECT id, Description_Rating, SN_description INTO OUTFILE '/tmp/SN_description_classification_desc_raiting.txt' 
FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'  from training_features_basic WHERE SN_description !='';

SELECT id, Website_Rating, SN_description INTO OUTFILE '/tmp/SN_description_classification_url_raiting.txt' 
FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'  from training_features_basic  WHERE SN_description !='';

/*test data*/
UPDATE venues SET description = REPLACE (description, '\n', '');


SELECT id, description INTO OUTFILE '/home/data/trec_challenge/classification/mallet-2.0.7/input_data/test_SN_description.txt' 
FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'  from venues;


create table desc_classification(id int, class_1 int, prob_class_1 double, class_2 int, prob_class_2 double, 
class_3 int, prob_class_3 double, class_4 int, prob_class_4 double, class_5 int, prob_class_5 double, class_6 int, prob_class_6 double);

create table url_classification(id int, class_1 int, prob_class_1 double, class_2 int, prob_class_2 double, 
class_3 int, prob_class_3 double, class_4 int, prob_class_4 double, class_5 int, prob_class_5 double, class_6 int, prob_class_6 double);

LOAD DATA INFILE '/home/data/trec_challenge/classification/mallet-2.0.7/classified_data/desc.txt' INTO TABLE desc_classification COLUMNS TERMINATED BY '\t' LINES TERMINATED BY '\n';
OLAD DATA INFILE '/home/data/trec_challenge/classification/mallet-2.0.7/classified_data/url.txt' INTO TABLE url_classification COLUMNS TERMINATED BY '\t' LINES TERMINATED BY '\n';


create table descriptionSentiment (Attraction_Id int, Desc_sentiment_score DOUBLE);
LOAD DATA INFILE '/home/data/trec_challenge/sentiment/senti_venues.txt' INTO TABLE 
descriptionSentiment  COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n';


Create Table score_net (place_id INT, profile_id INT, score DOUBLE);
Create Table score_rfores (place_id INT, profile_id INT, score DOUBLE);
Create Table score_lambda (place_id INT, profile_id INT, score DOUBLE);


LOAD DATA INFILE '/home/data/trec_challenge/RankLibInputs/output_Net.txt' 
INTO TABLE score_net COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n';

LOAD DATA INFILE '/home/data/trec_challenge/RankLibInputs/output_RForest.txt' 
INTO TABLE score_rforest COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n';

LOAD DATA INFILE '/home/data/trec_challenge/RankLibInputs/output_Lambda.txt' 
INTO TABLE score_lambda COLUMNS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n';

CREATE TABLE result_net AS SELECT t2.place_id, t2.profile_id, t1.context_id, t1.name as title, 
t1.description, t1.url, t2.score FROM venues t1 INNER JOIN score_net t2 ON t2.place_id = t1.id
order by profile_id;

CREATE TABLE result_rforest AS SELECT t2.place_id, t2.profile_id, t1.context_id, t1.name as title, 
t1.description, t1.url, t2.score FROM venues t1 INNER JOIN score_rforest t2 ON t2.place_id = t1.id
order by profile_id;


CREATE TABLE result_lambda AS SELECT t2.place_id, t2.profile_id, t1.context_id, t1.name as title, 
t1.description, t1.url, t2.score FROM venues t1 INNER JOIN score_lambda t2 ON t2.place_id = t1.id
order by profile_id;