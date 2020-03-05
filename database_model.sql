DROP DATABASE IF EXISTS everydaychef;
CREATE DATABASE everydaychef charset 'utf8';
USE everydaychef;

CREATE TABLE families(
	Id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users(
	Id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255),
    family_id INT NOT NULL,
    account_type CHAR DEFAULT 'l', ## local, can also be 'g' for google or 'f' for facebook account
    FOREIGN KEY(family_id) REFERENCES families(Id) ON DELETE CASCADE
);

CREATE TABLE user_invitations(
	Id INT AUTO_INCREMENT NOT NULL,
    user_id INT NOT NULL,
    family_id INT NOT NULL,
    PRIMARY KEY(Id, user_id, family_id),
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(family_id) REFERENCES families(id)
);

CREATE TABLE recipes(
	Id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL,
    creator_id INT,
    description TEXT,
    picture_url VARCHAR(255),
    number_of_likes INT NOT NULL DEFAULT 0,
    FOREIGN KEY(creator_id) REFERENCES users(Id) ON DELETE SET NULL
);

CREATE TABLE liked_recipes(
	Id INT AUTO_INCREMENT NOT NULL,
	user_id INT NOT NULL,
    recipe_id INT NOT NULL,
    PRIMARY KEY(Id, user_id, recipe_id),
	FOREIGN KEY(user_id) REFERENCES users(Id) ON DELETE CASCADE,
	FOREIGN KEY(recipe_id) REFERENCES recipes(Id) ON DELETE CASCADE
);

CREATE TABLE ingredients(
	Id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) UNIQUE,
    picture_url VARCHAR(255)
);

CREATE TABLE recipe_ingredients(
	Id INT AUTO_INCREMENT NOT NULL,
	recipe_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity_value INT NOT NULL,
    quantity_unit VARCHAR(100) NOT NULL,
    PRIMARY KEY(Id, recipe_id, ingredient_id),
    FOREIGN KEY(recipe_id) REFERENCES recipes(Id) ON DELETE CASCADE,
    FOREIGN KEY(ingredient_id) REFERENCES ingredients(Id) ON DELETE CASCADE
);

CREATE TABLE family_ingredients(
	Id INT AUTO_INCREMENT NOT NULL,
	family_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    quantity_value INT NOT NULL,
    quantity_unit VARCHAR(100) NOT NULL,
    PRIMARY KEY(Id, family_id, ingredient_id),
    FOREIGN KEY(family_id) REFERENCES families(Id) ON DELETE CASCADE,
    FOREIGN KEY(ingredient_id) REFERENCES ingredients(Id) ON DELETE CASCADE
);

DELIMITER //
CREATE TRIGGER liked_recipes_trigger BEFORE INSERT ON liked_recipes
	FOR EACH ROW 
	BEGIN 
		UPDATE recipes 
		SET number_of_likes = number_of_likes + 1
		WHERE Id = NEW.recipe_id;
	END;
//
DELIMITER ;

