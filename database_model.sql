Create Database EverydayChef;
use EverydayChef;

Create Table Users(
	Id Int primary key auto_increment not null,
    Name varchar(109) not null,
    Password varchar(255)
);

Create Table Families(
	Id Int Primary Key auto_increment not null,
    Name varchar(255) not null
);

Create Table UsersFamilies(
	UserId Int not null,
    FamilyId Int not null,
    Foreign key(UserId) references Users(Id) on delete cascade,
    Foreign key(FamilyId) references Families(Id) on delete cascade
);

Create Table Recipes(
	Id Int not null auto_increment primary key,
    Name varchar(200) not null,
    CreatorId int,
    foreign key(CreatorId) references Users(Id) on delete set null,
    Description text,
    PictureUrl varchar(255)
);

Create Table Ingredients(
	Id int not null auto_increment primary key,
    Name varchar(100),
    PictureUrl varchar(255)
);

Create Table RecipeIngredients(
	RecipeId Int not null,
    IngredientId Int not null,
    QuantityValue int not null,
    QuantityUnit varchar(100) not null,
    foreign key(RecipeId) references Recipes(Id) on delete cascade,
    foreign key(IngredientId) references Ingredients(Id) on delete cascade
);

Create Table FamilyIngredients(
	FamilyId int not null,
    IngredientId int not null,
    QuantityValue int not null,
    QuantityUnit varchar(100) not null,
    foreign key(FamilyId) references Families(Id) on delete cascade,
    foreign key(IngredientId) references Ingredients(Id) on delete cascade
);
