USE demo;

CREATE TABLE `users` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `tenant_id` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

Insert INTO users (name, tenant_id) Values ('hello', '19065dc6-a14a-11ed-ba3f-864b77a22e79'), ('world', '218a3e36-a14a-11ed-ba3f-864b77a22e79');
