CREATE DATABASE `parserdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE parserdb.`server_access_log` (
  `primarykey` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) NOT NULL,
  `date` timestamp(1) NOT NULL DEFAULT CURRENT_TIMESTAMP(1) ON UPDATE CURRENT_TIMESTAMP(1),
  `request` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`primarykey`),
  UNIQUE KEY `primarykey_UNIQUE` (`primarykey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE parserdb.`server_log` (
  `primaryKey` int(11) NOT NULL AUTO_INCREMENT,
  `IP` varchar(45) NOT NULL,
  `comment` varchar(255) NOT NULL,
  PRIMARY KEY (`primaryKey`),
  UNIQUE KEY `primaryKey_UNIQUE` (`primaryKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
s