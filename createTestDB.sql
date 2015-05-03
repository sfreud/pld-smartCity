create database if not exists testPLD;
use testPLD;
create table if not exists users(id integer auto_increment primary key not null,name varchar(50), password blob);
ALTER TABLE users AUTO_INCREMENT=0;
insert into users (name, password) values ("test","test");
insert into users (name, password) values ("sylvain","pass");
 
-- format utilisé pour le lieu ? coord gps, adresse ?
create table if not exists calendarEvents(id integer primary key not null,userid int not null,title varchar(50), location varchar(50), eventdate timestamp, foreign key (userid) references users(id));
insert into calendarevents(id,userid,title,location,eventdate) values (0,1,"acheter des crêpes","42 rue osef",'2015-05-01 00:00:01');
alter table calendarevents add unique (userid, title);
 
create table if not exists itineraries(id int primary key not null, userid int not null, tranportmodes char(3), departure time, arrival time, calculatedduration time, departurelocation varchar(50), arrivallocation varchar(50),foreign key (userid) references users(id));
insert into itineraries(id,userid,tranportmodes,departure,arrival,calculatedduration,departurelocation,arrivallocation) values (0,1,011,'08:30:00','09:00:00','00:00:20',"maison","boulot");
	
 
-- drop database testpld;
select * from calendarevents;
