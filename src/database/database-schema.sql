BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Admins" (
	"id"	INTEGER NOT NULL UNIQUE,
	"username"	TEXT NOT NULL,
	"password"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Bills" (
	"id"	INTEGER NOT NULL UNIQUE,
	"reservation_details"	INTEGER NOT NULL,
	"sub_total"	NUMERIC NOT NULL,
	"hst"	NUMERIC NOT NULL,
	"total"	NUMERIC NOT NULL,
	"discount"	NUMERIC,
	"payment_detail"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Guests" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT NOT NULL,
	"phone_number"	INTEGER NOT NULL,
	"address"	TEXT NOT NULL,
	"email"	TEXT NOT NULL,
	"vin_number"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Payment_Details" (
	"card_number"	INTEGER NOT NULL UNIQUE,
	"card_type"	TEXT NOT NULL,
	"csc"	INTEGER NOT NULL,
	"exp_date"	TEXT NOT NULL,
	"card_holder"	INTEGER NOT NULL,
	PRIMARY KEY("card_number")
);
CREATE TABLE IF NOT EXISTS "Reservations" (
	"id"	INTEGER NOT NULL UNIQUE,
	"guest"	INTEGER NOT NULL,
	"bill"	INTEGER NOT NULL,
	"check_in_date"	TEXT NOT NULL,
	"check_out_date"	TEXT NOT NULL,
	"total_guests"	INTEGER NOT NULL,
	"status"	TEXT NOT NULL,
	"admin"	INTEGER NOT NULL,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Rooms" (
	"id"	INTEGER NOT NULL UNIQUE,
	"room_type"	TEXT NOT NULL,
	"num_of_beds"	INTEGER NOT NULL,
	"price"	NUMERIC NOT NULL,
	"reservation_id"	INTEGER,
	"status"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
INSERT INTO "Admins" ("id","username","password") VALUES (1,'name01','password01');
INSERT INTO "Admins" ("id","username","password") VALUES (2,'name02','password02');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (1,'single',1,100,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (2,'single',1,100,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (3,'single',1,150.5,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (4,'single',1,175.99,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (5,'double',2,220,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (6,'double',2,250.55,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (7,'double',2,225.5,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (8,'double',2,200,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (9,'delux',1,400,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (10,'delux',1,450.5,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (11,'delux',1,475.5,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (12,'delux',1,425,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (13,'pent-house',1,1500.5,NULL,'available');
INSERT INTO "Rooms" ("id","room_type","num_of_beds","price","reservation_id","status") VALUES (14,'pent-house',1,1550,NULL,'available');
COMMIT;
