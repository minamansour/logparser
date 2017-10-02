#(1) Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.

SELECT count(SAL.primarykey) AS IP_COUNT, SAL.IP FROM parserdb.server_access_log SAL 
Where SAL.date >= '2017-01-01 13:00:00' AND SAL.date <= '2017-01-01 14:00:00' 
GROUP BY SAL.IP 
HAVING count(SAL.primarykey) > 100
;

#(2) Write MySQL query to find requests made by a given IP
SELECT IP , request FROM  parserdb.server_access_log where IP = '127.0.0.1';