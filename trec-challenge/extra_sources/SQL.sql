CREATE table profiles2venues AS
SELECT t1.Profile_Id, t1.Attraction_Id, t1.Description_Rating, t1.Website_Rating, 
t2.Title, t2.Description, t2.Url FROM profiles t1 INNER JOIN examples t2 
ON (t1.Attraction_Id = t2.Attraction_Id);

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

CREATE table trainingProfiles2Examples AS
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

CREATE TABLE trainingFeatures AS
SELECT t1.*, 
t2.name,
t2.id,
t2.foursquare_score,
t2.yelp_score,
t2.lat,
t2.lng,
t2.description AS SN_description, 
t2.url,
t2.foursquare_id,
t2.facebook_id,
t2.yelp_id
FROM trainingProfiles2Examples t1 INNER JOIN trainingVenues t2 
ON t1.name like t2.Title;

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