DROP DATABASE IF EXISTS comfygo;
CREATE DATABASE IF NOT EXISTS comfygo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE comfygo;

-- ===================== USERS =====================
CREATE TABLE IF NOT EXISTS users (
  userid VARCHAR(12) PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  useremail VARCHAR(100) NOT NULL,
  userpassword VARCHAR(255) NOT NULL,
  userphone VARCHAR(20) UNIQUE,
  usernid VARCHAR(20) UNIQUE,
  passportno VARCHAR(20),
  dateofbirth DATE,
  country VARCHAR(80),
  address VARCHAR(255),
  registrationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_users_email (useremail),
  INDEX idx_users_email (useremail),
  INDEX idx_users_phone (userphone),
  INDEX idx_users_nid (usernid),
  INDEX idx_users_passport (passportno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== MANAGERS =====================
CREATE TABLE IF NOT EXISTS managers (
  managerid VARCHAR(12) PRIMARY KEY,
  managername VARCHAR(100) NOT NULL,
  manageremail VARCHAR(100) NOT NULL,
  managerphone VARCHAR(20) NOT NULL,
  managerpassword VARCHAR(255) NOT NULL,
  hotelname VARCHAR(150),
  hotelnid VARCHAR(60) UNIQUE,
  registrationnumber VARCHAR(60) UNIQUE,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  registrationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_managers_email (manageremail),
  INDEX idx_managers_email (manageremail),
  INDEX idx_managers_phone (managerphone),
  INDEX idx_managers_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== GUIDES =====================
CREATE TABLE IF NOT EXISTS guides (
  guideid VARCHAR(12) PRIMARY KEY,
  guidename VARCHAR(100) NOT NULL,
  guideemail VARCHAR(100) UNIQUE NOT NULL,
  guidephone VARCHAR(20) UNIQUE NOT NULL,
  guidepassword VARCHAR(255) NOT NULL,
  guidedivision VARCHAR(60),
  guidedistrict VARCHAR(60),
  guidelanguage VARCHAR(200),
  specialization VARCHAR(120),
  rating DOUBLE DEFAULT 5.0,
  totalratings INT DEFAULT 0,
  isavailable BOOLEAN DEFAULT TRUE,
  yearexperience INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  registrationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_guides_email (guideemail),
  INDEX idx_guides_phone (guidephone),
  INDEX idx_guides_available (isavailable),
  INDEX idx_guides_division (guidedivision),
  INDEX idx_guides_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== HOTELS =====================
CREATE TABLE IF NOT EXISTS hotels (
  hotelid VARCHAR(12) PRIMARY KEY,
  hotelname VARCHAR(150) NOT NULL,
  hotellocation VARCHAR(150),
  hotelpricepernight DOUBLE NOT NULL,
  hotelrating DOUBLE DEFAULT 0,
  roomavailability INT DEFAULT 0,
  roomcategory VARCHAR(60),
  totalrooms INT DEFAULT 0,
  hotelfeatures VARCHAR(255),
  managerid VARCHAR(12),
  registrationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_hotels_manager
    FOREIGN KEY (managerid) REFERENCES managers(managerid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_hotels_location (hotellocation),
  INDEX idx_hotels_rating (hotelrating),
  INDEX idx_hotels_manager (managerid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== ROOMS =====================
CREATE TABLE IF NOT EXISTS rooms (
  roomid VARCHAR(12) PRIMARY KEY,
  roomnumber VARCHAR(10) NOT NULL,
  hotelid VARCHAR(12) NOT NULL,
  roomtype VARCHAR(50),
  roomprice DECIMAL(10,2),
  roomcapacity INT,
  isavailable BOOLEAN DEFAULT TRUE,
  roomfeatures VARCHAR(200),
  CONSTRAINT fk_rooms_hotel
    FOREIGN KEY (hotelid) REFERENCES hotels(hotelid)
    ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_rooms_hotel (hotelid),
  INDEX idx_rooms_available (isavailable)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== TOURIST SPOTS =====================
CREATE TABLE IF NOT EXISTS touristspots (
  spotid VARCHAR(12) PRIMARY KEY,
  spotname VARCHAR(150) NOT NULL,
  division VARCHAR(60),
  district VARCHAR(60),
  spotaddress VARCHAR(200),
  description TEXT,
  entryfee DOUBLE DEFAULT 0,
  rating DOUBLE DEFAULT 0,
  totalvisitors INT DEFAULT 0,
  bestseason VARCHAR(100),
  visitinghours VARCHAR(100),
  registrationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_spots_division (division),
  INDEX idx_spots_district (district),
  INDEX idx_spots_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== PAYMENT =====================
CREATE TABLE IF NOT EXISTS payment (
  paymentid VARCHAR(12) PRIMARY KEY,
  amount DOUBLE NOT NULL,
  paymentmethod VARCHAR(40),
  paymentstatus VARCHAR(30) DEFAULT 'PENDING',
  transactionid VARCHAR(80) UNIQUE,
  description VARCHAR(255),
  paymentdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_payment_status (paymentstatus),
  INDEX idx_payment_date (paymentdate),
  INDEX idx_payment_transaction (transactionid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== BOOKING =====================
CREATE TABLE IF NOT EXISTS booking (
  bookingid VARCHAR(12) PRIMARY KEY,
  userid VARCHAR(12),
  checkindate DATE,
  checkoutdate DATE,
  totalprice DOUBLE NOT NULL,
  bookingstatus VARCHAR(30) DEFAULT 'PENDING',
  paymentid VARCHAR(12),
  hotelname VARCHAR(150),
  hotellocation VARCHAR(150),
  guidename VARCHAR(150),
  guideid VARCHAR(12),
  numberofrooms INT DEFAULT 0,
  bookingdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_booking_user
    FOREIGN KEY (userid) REFERENCES users(userid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_booking_payment
    FOREIGN KEY (paymentid) REFERENCES payment(paymentid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_booking_guide
    FOREIGN KEY (guideid) REFERENCES guides(guideid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_booking_user (userid),
  INDEX idx_booking_status (bookingstatus),
  INDEX idx_booking_payment (paymentid),
  INDEX idx_booking_checkin (checkindate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== TRANSPORT BOOKING =====================
CREATE TABLE IF NOT EXISTS transportbooking (
  ticketid VARCHAR(12) PRIMARY KEY,
  userid VARCHAR(12),
  transporttype VARCHAR(50),
  departurelocation VARCHAR(100),
  arrivallocation VARCHAR(100),
  departuredate DATE,
  arrivaldate DATE,
  numberofpassengers INT,
  seatnumber VARCHAR(20),
  bookingstatus VARCHAR(30) DEFAULT 'PENDING',
  fare DOUBLE,
  vehicleregistration VARCHAR(20),
  vehiclecompany VARCHAR(100),
  bookingdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transport_user
    FOREIGN KEY (userid) REFERENCES users(userid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_transport_user (userid),
  INDEX idx_transport_status (bookingstatus),
  INDEX idx_transport_departure (departuredate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== GUIDE BOOKING =====================
CREATE TABLE IF NOT EXISTS guidebooking (
  bookingid VARCHAR(12) PRIMARY KEY,
  userid VARCHAR(12),
  guideid VARCHAR(12),
  bookingdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  tourdurationdays INT NOT NULL,
  tourpurpose VARCHAR(100),
  tourlocation VARCHAR(100),
  tourstatus VARCHAR(30) DEFAULT 'PENDING',
  guidefee DOUBLE,
  paymentstatus VARCHAR(30) DEFAULT 'PENDING',
  specialrequest VARCHAR(200),
  CONSTRAINT fk_guidebooking_user
    FOREIGN KEY (userid) REFERENCES users(userid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_guidebooking_guide
    FOREIGN KEY (guideid) REFERENCES guides(guideid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_guidebooking_user (userid),
  INDEX idx_guidebooking_guide (guideid),
  INDEX idx_guidebooking_status (tourstatus)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== RATINGS =====================
CREATE TABLE IF NOT EXISTS ratings (
  ratingid VARCHAR(12) PRIMARY KEY,
  userid VARCHAR(12),
  ratingtype VARCHAR(20),
  targetname VARCHAR(150),
  rating INT,
  review VARCHAR(500),
  ratingdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ratings_user
    FOREIGN KEY (userid) REFERENCES users(userid)
    ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_ratings_user (userid),
  INDEX idx_ratings_type (ratingtype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== WEATHER =====================
CREATE TABLE IF NOT EXISTS weather (
  weatherid INT AUTO_INCREMENT PRIMARY KEY,
  division VARCHAR(60),
  temperature DOUBLE,
  weathercondition VARCHAR(50),
  humidity INT,
  windspeed DOUBLE,
  lastupdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_weather_division (division)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== FOOD MENU =====================
CREATE TABLE IF NOT EXISTS foodmenu (
  menuid VARCHAR(12) PRIMARY KEY,
  hotelid VARCHAR(12),
  foodname VARCHAR(100),
  foodcategory VARCHAR(50),
  foodprice DECIMAL(10,2),
  fooddescription VARCHAR(200),
  isavailable BOOLEAN DEFAULT TRUE,
  cuisinetype VARCHAR(50),
  isvegetarian BOOLEAN DEFAULT FALSE,
  CONSTRAINT fk_foodmenu_hotel
    FOREIGN KEY (hotelid) REFERENCES hotels(hotelid)
    ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX idx_foodmenu_hotel (hotelid),
  INDEX idx_foodmenu_category (foodcategory)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================== AUDIT + SESSION LOGS =====================
CREATE TABLE IF NOT EXISTS auditlogs (
  logid INT AUTO_INCREMENT PRIMARY KEY,
  eventtype VARCHAR(60) NOT NULL,
  userid VARCHAR(12),
  email VARCHAR(120),
  category VARCHAR(30),
  details VARCHAR(500),
  ipaddress VARCHAR(45),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_audit_user (userid),
  INDEX idx_audit_email (email),
  INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS session_logs (
  logid INT AUTO_INCREMENT PRIMARY KEY,
  eventtype VARCHAR(60) NOT NULL,
  userid VARCHAR(12),
  details VARCHAR(255),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_session_user (userid),
  INDEX idx_session_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
-- ===================== SEED DATA =========================
-- =========================================================

INSERT INTO managers
(managerid, managername, manageremail, managerphone, managerpassword, hotelname, hotelnid, registrationnumber, status, registrationdate)
VALUES
('MGR001', 'Ahmed Khan', 'ahmed.khan@hotelgroup.com', '01711111001', 'pass@123456', 'Green Valley Resort', 'NID123001', 'REG001', 'ACTIVE', NOW()),
('MGR002', 'Rajesh Kumar', 'rajesh.k@hotelgroup.com', '01711111002', 'pass@123456', 'Beach Paradise Hotels', 'NID123002', 'REG002', 'ACTIVE', NOW()),
('MGR003', 'Fatima Islam', 'fatima.islam@hotelgroup.com', '01711111003', 'pass@123456', 'Dhaka Heritage Hotel', 'NID123003', 'REG003', 'ACTIVE', NOW()),
('MGR004', 'Kamal Hassan', 'kamal.hassan@hotelgroup.com', '01711111004', 'pass@123456', 'Sundarban Safari Lodge', 'NID123004', 'REG004', 'ACTIVE', NOW()),
('MGR005', 'Rafi Ahmed', 'rafi.ahmed@hotelgroup.com', '01711111005', 'pass@123456', 'Chittagong Bay Hotel', 'NID123005', 'REG005', 'ACTIVE', NOW());

INSERT INTO hotels
(hotelid, hotelname, hotellocation, hotelpricepernight, hotelrating, roomavailability, roomcategory, totalrooms, hotelfeatures, managerid, registrationdate)
VALUES
('HTL001', 'Green Valley Resort', 'Cox''s Bazar', 3500.00, 4.8, 25, 'Luxury', 30, 'Sea view, AC, WiFi, Pool, Spa', 'MGR001', NOW()),
('HTL002', 'Beach Paradise Hotels', 'Cox''s Bazar', 2800.00, 4.7, 18, 'Standard', 20, 'Beach access, AC, WiFi, Restaurant', 'MGR002', NOW()),
('HTL003', 'Dhaka Heritage Hotel', 'Dhaka', 4200.00, 4.9, 32, 'Luxury', 40, 'Heritage decor, WiFi, Restaurant, Gym', 'MGR003', NOW()),
('HTL004', 'Sundarban Safari Lodge', 'Khulna', 3800.00, 4.8, 20, 'Deluxe', 25, 'Eco-friendly, Boat tours, WiFi, Nature walks', 'MGR004', NOW()),
('HTL005', 'Chittagong Bay Hotel', 'Chittagong', 3600.00, 4.7, 22, 'Deluxe', 28, 'Bay view, AC, WiFi, Gym, Conference Hall', 'MGR005', NOW()),
('HTL006', 'Sylhet Tea Estate Hotel', 'Sylhet', 2500.00, 4.6, 15, 'Standard', 18, 'Garden view, AC, WiFi, Tea house', NULL, NOW()),
('HTL007', 'Mountain View Resort', 'Bandarban', 2200.00, 4.5, 12, 'Standard', 15, 'Hill view, Trekking, WiFi, Restaurant', NULL, NOW()),
('HTL008', 'Rangamati Hill Resort', 'Rangamati', 2100.00, 4.4, 14, 'Standard', 17, 'Lake view, AC, WiFi, Boat rental', NULL, NOW()),
('HTL009', 'Saint Martin Beach Resort', 'Saint Martin', 3100.00, 4.6, 16, 'Deluxe', 20, 'Beach, Water sports, WiFi, Diving center', NULL, NOW()),
('HTL010', 'Kuakata Beach Resort', 'Kuakata', 2400.00, 4.5, 13, 'Standard', 16, 'Beach, Sunset view, WiFi, Restaurant', NULL, NOW());

INSERT INTO rooms
(roomid, roomnumber, hotelid, roomtype, roomprice, roomcapacity, isavailable, roomfeatures)
VALUES
('ROOM001', '101', 'HTL001', 'Deluxe', 3500.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),
('ROOM002', '102', 'HTL001', 'Deluxe', 3500.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),
('ROOM003', '103', 'HTL001', 'Suite', 5000.00, 3, TRUE, 'Sea view, AC, Hot water, WiFi, Kitchenette'),
('ROOM004', '104', 'HTL001', 'Standard', 2500.00, 2, FALSE, 'AC, Hot water, WiFi, TV'),
('ROOM005', '105', 'HTL001', 'Deluxe', 3500.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),

('ROOM006', '201', 'HTL002', 'Standard', 2800.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM007', '202', 'HTL002', 'Standard', 2800.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM008', '203', 'HTL002', 'Deluxe', 3500.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),
('ROOM009', '204', 'HTL002', 'Standard', 2800.00, 2, FALSE, 'AC, Hot water, WiFi, TV'),
('ROOM010', '205', 'HTL002', 'Standard', 2800.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),

('ROOM011', '301', 'HTL003', 'Suite', 6000.00, 3, TRUE, 'City view, AC, Hot water, WiFi, Kitchenette'),
('ROOM012', '302', 'HTL003', 'Deluxe', 4200.00, 2, TRUE, 'City view, AC, Hot water, WiFi, TV'),
('ROOM013', '303', 'HTL003', 'Deluxe', 4200.00, 2, TRUE, 'City view, AC, Hot water, WiFi, TV'),
('ROOM014', '304', 'HTL003', 'Standard', 2800.00, 2, FALSE, 'AC, Hot water, WiFi, TV'),
('ROOM015', '305', 'HTL003', 'Suite', 6000.00, 3, TRUE, 'City view, AC, Hot water, WiFi, Kitchenette'),

('ROOM016', '401', 'HTL004', 'Deluxe', 3800.00, 2, TRUE, 'Forest view, AC, Hot water, WiFi, TV'),
('ROOM017', '402', 'HTL004', 'Deluxe', 3800.00, 2, TRUE, 'Forest view, AC, Hot water, WiFi, TV'),
('ROOM018', '403', 'HTL004', 'Suite', 5500.00, 3, TRUE, 'Forest view, AC, Hot water, WiFi, Kitchenette'),
('ROOM019', '404', 'HTL004', 'Standard', 2500.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM020', '405', 'HTL004', 'Standard', 2500.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),

('ROOM021', '501', 'HTL005', 'Deluxe', 3600.00, 2, TRUE, 'Bay view, AC, Hot water, WiFi, TV'),
('ROOM022', '502', 'HTL005', 'Deluxe', 3600.00, 2, FALSE, 'Bay view, AC, Hot water, WiFi, TV'),
('ROOM023', '503', 'HTL005', 'Suite', 5000.00, 3, TRUE, 'Bay view, AC, Hot water, WiFi, Kitchenette'),
('ROOM024', '504', 'HTL005', 'Standard', 2400.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),

('ROOM025', '601', 'HTL006', 'Deluxe', 2500.00, 2, TRUE, 'Garden view, AC, Hot water, WiFi, TV'),
('ROOM026', '602', 'HTL006', 'Standard', 1800.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM027', '603', 'HTL006', 'Deluxe', 2500.00, 2, TRUE, 'Garden view, AC, Hot water, WiFi, TV'),

('ROOM028', '701', 'HTL007', 'Deluxe', 2200.00, 2, TRUE, 'Hill view, AC, Hot water, WiFi, TV'),
('ROOM029', '702', 'HTL007', 'Standard', 1500.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM030', '703', 'HTL007', 'Deluxe', 2200.00, 2, TRUE, 'Hill view, AC, Hot water, WiFi, TV'),

('ROOM031', '801', 'HTL008', 'Deluxe', 2100.00, 2, TRUE, 'Lake view, AC, Hot water, WiFi, TV'),
('ROOM032', '802', 'HTL008', 'Standard', 1400.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM033', '803', 'HTL008', 'Deluxe', 2100.00, 2, FALSE, 'Lake view, AC, Hot water, WiFi, TV'),

('ROOM034', '901', 'HTL009', 'Deluxe', 3100.00, 2, TRUE, 'Beach view, AC, Hot water, WiFi, TV'),
('ROOM035', '902', 'HTL009', 'Deluxe', 3100.00, 2, TRUE, 'Beach view, AC, Hot water, WiFi, TV'),
('ROOM036', '903', 'HTL009', 'Suite', 4500.00, 3, TRUE, 'Beach view, AC, Hot water, WiFi, Kitchenette'),

('ROOM037', '1001', 'HTL010', 'Deluxe', 2400.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),
('ROOM038', '1002', 'HTL010', 'Standard', 1600.00, 2, TRUE, 'AC, Hot water, WiFi, TV'),
('ROOM039', '1003', 'HTL010', 'Deluxe', 2400.00, 2, TRUE, 'Sea view, AC, Hot water, WiFi, TV'),
('ROOM040', '1004', 'HTL010', 'Standard', 1600.00, 2, FALSE, 'AC, Hot water, WiFi, TV');

INSERT INTO touristspots
(spotid, spotname, division, district, spotaddress, description, entryfee, rating, totalvisitors, bestseason, visitinghours)
VALUES
('SPOT001', 'Cox''s Bazar Beach', 'Chittagong', 'Cox''s Bazar', 'Cox''s Bazar Sadar', 'World''s longest unbroken sandy beach (120 km) with golden sands and crystal clear waters', 0.00, 4.9, 15000, 'October - March', '6:00 AM - 6:00 PM'),
('SPOT002', 'Lalbagh Fort', 'Dhaka', 'Dhaka', 'Lalbagh, Dhaka', 'Historic 17th century Mughal fort with stunning architecture and museum', 100.00, 4.7, 8500, 'October - March', '9:00 AM - 5:00 PM'),
('SPOT003', 'Ahsan Manzil', 'Dhaka', 'Dhaka', 'Arambagh, Dhaka', 'Pink Palace - former residence of Dhaka Nawabs with rich history', 100.00, 4.8, 7500, 'Year-round', '9:00 AM - 5:00 PM'),
('SPOT004', 'Sundarbans National Park', 'Khulna', 'Khulna', 'Khulna', 'World''s largest mangrove forest with Bengal tigers and diverse wildlife', 5000.00, 4.9, 5000, 'November - February', '8:00 AM - 4:00 PM'),
('SPOT005', 'Sylhet Tea Gardens', 'Sylhet', 'Sylhet', 'Sylhet Sadar', 'Beautiful tea gardens with rolling hills, green valleys and tea production tours', 0.00, 4.8, 12000, 'January - April', '7:00 AM - 6:00 PM'),
('SPOT006', 'Saint Martin Island', 'Chittagong', 'Cox''s Bazar', 'Saint Martin Island', 'Scenic island with coral reefs, pristine beaches and water sports activities', 0.00, 4.7, 8000, 'October - March', '6:00 AM - 6:00 PM'),
('SPOT007', 'Bandarban Hill Tracts', 'Chittagong', 'Bandarban', 'Bandarban Sadar', 'Beautiful hill stations with tribal culture, traditional crafts and trekking routes', 0.00, 4.8, 6500, 'November - February', '8:00 AM - 5:00 PM'),
('SPOT008', 'Rangamati Lake', 'Chittagong', 'Rangamati', 'Rangamati Sadar', 'Scenic artificial lake surrounded by green hills with boat tours and nature walks', 0.00, 4.6, 7000, 'October - March', '8:00 AM - 5:00 PM'),
('SPOT009', 'Kuakata Beach', 'Barisal', 'Patuakhali', 'Kuakata', 'Beautiful beach where you can see both sunrise and sunset, unique in Bangladesh', 0.00, 4.6, 6500, 'September - April', '6:00 AM - 7:00 PM'),
('SPOT010', 'Bagerhat Mosque City', 'Khulna', 'Bagerhat', 'Bagerhat Sadar', 'UNESCO World Heritage Site with 60 Dome Mosque and ancient Islamic architecture', 100.00, 4.8, 3500, 'October - March', '9:00 AM - 5:00 PM'),
('SPOT011', 'Paharpur Buddhist Monastery', 'Rajshahi', 'Naogaon', 'Paharpur', 'Ancient Buddhist temple complex, UNESCO World Heritage Site with historical significance', 100.00, 4.7, 2500, 'Year-round', '9:00 AM - 5:00 PM'),
('SPOT012', 'Srimangal Tea Estate', 'Sylhet', 'Moulvibazar', 'Srimangal', 'Famous tea gardens with wildlife sanctuary and Lawachara National Park nearby', 0.00, 4.7, 9000, 'January - April', '8:00 AM - 6:00 PM'),
('SPOT013', 'Kaptai Lake', 'Chittagong', 'Rangamati', 'Rangamati Sadar', 'Largest artificial lake in Bangladesh surrounded by picturesque hills and forests', 0.00, 4.6, 5500, 'October - March', '8:00 AM - 5:00 PM'),
('SPOT014', 'Bangladesh National Museum', 'Dhaka', 'Dhaka', 'Shahbag, Dhaka', 'Museum with historical artifacts spanning prehistoric to modern times', 100.00, 4.5, 10000, 'Year-round', '10:00 AM - 5:00 PM'),
('SPOT015', 'Star Mosque', 'Dhaka', 'Dhaka', 'Old Dhaka', 'Beautiful mosque with intricate star-patterned interior design and traditional architecture', 0.00, 4.4, 6000, 'Year-round', '24 hours'),
('SPOT016', 'Sonargaon', 'Dhaka', 'Sonargaon', 'Sonargaon Sadar', 'Old capital of Bengal with folk museum, Panam City and historical monuments', 100.00, 4.5, 4000, 'October - March', '9:00 AM - 5:00 PM'),
('SPOT017', 'Jaflong Stone Area', 'Sylhet', 'Sylhet', 'Jaflong', 'Scenic area with stone collection activities, clear river and border views', 0.00, 4.5, 5500, 'January - April', '8:00 AM - 6:00 PM'),
('SPOT018', 'Chittagong War Cemetery', 'Chittagong', 'Chittagong', 'Chittagong Sadar', 'WWII cemetery with historical graves and significant war history', 0.00, 4.3, 2000, 'Year-round', '9:00 AM - 5:00 PM'),
('SPOT019', 'Tangail Textile Center', 'Dhaka', 'Tangail', 'Tangail Sadar', 'Famous for traditional saris, handlooms and textile production centers', 0.00, 4.5, 3500, 'Year-round', '9:00 AM - 5:00 PM'),
('SPOT020', 'Ratargul Swamp Forest', 'Sylhet', 'Sylhet', 'Golapganj', 'Unique freshwater swamp forest with boat tours through pristine nature', 0.00, 4.7, 4000, 'August - December', '8:00 AM - 5:00 PM');

INSERT INTO weather (division, temperature, weathercondition, humidity, windspeed) VALUES
('Dhaka', 28.5, 'Clear', 65, 8.5),
('Chittagong', 29.5, 'Partly Cloudy', 72, 12.3),
('Khulna', 26.5, 'Clear', 68, 9.2),
('Rajshahi', 27.0, 'Sunny', 62, 7.8),
('Barisal', 28.0, 'Humid', 70, 10.5),
('Sylhet', 24.5, 'Rainy', 78, 6.2),
('Rangpur', 25.5, 'Clear', 60, 6.5),
('Mymensingh', 27.0, 'Partly Cloudy', 68, 8.0);

INSERT INTO foodmenu
(menuid, hotelid, foodname, foodcategory, foodprice, fooddescription, isavailable, cuisinetype, isvegetarian)
VALUES
('FOOD001', 'HTL001', 'Grilled Pomfret Fish', 'Seafood', 550.00, 'Fresh local pomfret grilled with lemon butter and spices', TRUE, 'Bangladeshi', FALSE),
('FOOD002', 'HTL001', 'Tiger Prawn Curry', 'Seafood', 650.00, 'Large tiger prawns in rich coconut and spice gravy with rice', TRUE, 'Bangladeshi', FALSE),
('FOOD003', 'HTL001', 'Biryani with Meat', 'Main Course', 400.00, 'Fragrant basmati rice with tender meat and aromatic spices', TRUE, 'Bangladeshi', FALSE),
('FOOD004', 'HTL001', 'Vegetable Samosa', 'Appetizer', 150.00, 'Crispy pastry with potato and pea filling, served with chutney', TRUE, 'Bangladeshi', TRUE),
('FOOD005', 'HTL002', 'Crab Masala Curry', 'Seafood', 700.00, 'Fresh crab in mustard and coconut sauce with rice', TRUE, 'Bangladeshi', FALSE),
('FOOD006', 'HTL002', 'Hilsa Fish Fry', 'Seafood', 550.00, 'National fish lightly spiced and deep fried to crispy perfection', TRUE, 'Bangladeshi', FALSE),
('FOOD007', 'HTL003', 'Tandoori Chicken', 'Main Course', 500.00, 'Marinated chicken cooked in clay oven with traditional spices', TRUE, 'Indian', FALSE),
('FOOD008', 'HTL003', 'Paneer Tikka', 'Appetizer', 400.00, 'Grilled cottage cheese with bell peppers and spiced yogurt', TRUE, 'Indian', TRUE),
('FOOD009', 'HTL003', 'Butter Chicken', 'Main Course', 480.00, 'Tender chicken in creamy tomato sauce with fresh cream', TRUE, 'Indian', FALSE),
('FOOD010', 'HTL004', 'Mud Crab Roast', 'Seafood', 850.00, 'Large mud crab roasted with garlic and spices', TRUE, 'Bangladeshi', FALSE),
('FOOD011', 'HTL004', 'Vegetable Bhaji', 'Vegetable', 250.00, 'Mixed seasonal vegetables in light spice gravy', TRUE, 'Bangladeshi', TRUE),
('FOOD012', 'HTL005', 'Beef Roast', 'Main Course', 550.00, 'Slow-cooked beef with traditional spices and herbs', TRUE, 'Bangladeshi', FALSE),
('FOOD013', 'HTL005', 'Dal Bhat', 'Main Course', 200.00, 'Traditional lentil rice with light curry and vegetables', TRUE, 'Bangladeshi', TRUE),
('FOOD014', 'HTL006', 'Sylhet Tea with Snacks', 'Beverage', 150.00, 'Famous Sylhet tea with traditional pastries and biscuits', TRUE, 'Bangladeshi', TRUE),
('FOOD015', 'HTL006', 'Vegetable Biryani', 'Main Course', 350.00, 'Fragrant rice with mixed vegetables and spices', TRUE, 'Bangladeshi', TRUE),
('FOOD016', 'HTL007', 'Chicken Curry', 'Main Course', 380.00, 'Tender chicken in aromatic spiced gravy with rice', TRUE, 'Bangladeshi', FALSE),
('FOOD017', 'HTL008', 'Fish Curry', 'Main Course', 420.00, 'Fresh water fish in mustard or coconut spiced sauce', TRUE, 'Bangladeshi', FALSE),
('FOOD018', 'HTL009', 'Shrimp Paste Rice', 'Main Course', 450.00, 'Rice cooked with dried shrimp paste and fresh herbs', TRUE, 'Bangladeshi', FALSE),
('FOOD019', 'HTL010', 'Hilsa Special Preparation', 'Seafood', 600.00, 'National fish prepared in traditional Bengali style with mustard seeds', TRUE, 'Bangladeshi', FALSE),
('FOOD020', 'HTL001', 'Mango Lassi', 'Beverage', 120.00, 'Fresh mango yogurt drink with spices', TRUE, 'Bangladeshi', TRUE),
('FOOD021', 'HTL003', 'Gulab Jamun', 'Dessert', 200.00, 'Sweet milk balls in sugar syrup, Indian traditional dessert', TRUE, 'Indian', TRUE),
('FOOD022', 'HTL005', 'Kheer', 'Dessert', 180.00, 'Rice pudding with condensed milk, nuts and cardamom', TRUE, 'Indian', TRUE),
('FOOD023', 'HTL006', 'Papayas with Honey', 'Dessert', 150.00, 'Fresh local papaya with honey and lime juice', TRUE, 'Bangladeshi', TRUE),
('FOOD024', 'HTL010', 'Sweetened Rice Cake', 'Dessert', 120.00, 'Traditional Bengali rice cake with jaggery and coconut', TRUE, 'Bangladeshi', TRUE);