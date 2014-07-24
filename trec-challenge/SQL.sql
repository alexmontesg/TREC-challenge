CREATE table profiles2venues AS
SELECT t1.Profile_Id, t1.Attraction_Id, t1.Description_Rating, t1.Website_Rating, 
t2.Title, t2.Description, t2.Url FROM profiles t1 INNER JOIN examples t2 
ON (t1.Attraction_Id = t2.Attraction_Id);


CREATE TABLE joinedVenues AS
SELECT t1.*,
t2.foursquare_id, t2.facebook_id, t2.google_id, t2.google_reference, t2.yelp_id,
t2.description AS SN_description, t2.score, t2.facebook_likes,
SUBSTRING_INDEX(t1.Url, '/', 3) AS url1,
SUBSTRING_INDEX(t2.url, '/', 3) AS url2
FROM profiles2venues t1 INNER JOIN venues t2 
ON (SUBSTRING_INDEX(SUBSTRING_INDEX(t1.Url, '/', 3), '.', 2) = SUBSTRING_INDEX(SUBSTRING_INDEX(t2.url, '/', 3), '.', 2));



 CREATE TABLE joinedVenues AS
 SELECT t1.*, t2.name, t2.url
 FROM profiles2venues t1 INNER JOIN venues t2 
 ON t1.name like t2.Title;
