create table category (
    id int primary key,
    name text not null,
    url text,
    display_name text,
    parent_id int
)

insert into category values
(rowid, 'Men', 'url', 'Men'),
(rowid, 'Women', 'url', 'Women'),

(rowid, 'Featured In', 'url', 'Featured In', select id from category where name = 'Men'),
(rowid, 'Featured In', 'url', 'Featured In', select id from category where name = 'Women'),
(rowid, 'Clothing', 'url', 'Clothing', select id from category where name = 'Men'),
(rowid, 'Clothing', 'url', 'Clothing', select id from category where name = 'Women'),
(rowid, 'Accessories & Grooming', 'url', 'Accessories & Grooming', select id from category where name = 'Men'),
(rowid, 'Accessories & Beauty', 'url', 'Accessories & Beauty', select id from category where name = 'Women'),

-- Men featured in
(rowid, 'New In', 'url', 'New In', 2),
(rowid, 'Best Sellers', 'url', 'Best Sellers', 2),
(rowid, 'Loungewear', 'url', 'Loungewear', 2),
(rowid, 'Good Denim', 'url', 'Good Denim', 2),
(rowid, 'Sale', 'url', 'Sale', 2),
(rowid, 'Gift Cards', 'url', 'Gift Cards', 2),

-- Men Clothing
(rowid, 'Shop All', 'url', 'Shop All', 9),
(rowid, 'Jackets & Outerwear', 'url', 'Jackets & Outerwear', 9),
(rowid, 'T-Shirts', 'url', 'T-Shirts', 9),
(rowid, 'Sweaters & Cardigans', 'url', 'Sweaters & Cardigans', 9),
(rowid, 'Denim', 'url', 'Denim', 9),
(rowid, 'Pants', 'url', 'Pants', 9),
(rowid, 'Shorts', 'url', 'Shorts', 9),
(rowid, 'Underwear', 'url', 'Underwear', 9),

-- Men Accessories & Grooming
(rowid, 'Hats & Caps', 'url', 'Accessories & Grooming', 1),
(rowid, 'Shoes & Boots', 'url', 'Shop All', 18),
(rowid, 'Socks', 'url', 'Jackets & Outerwear', 18),
(rowid, 'Watches', 'url', 'T-Shirts', 18),
(rowid, 'Bags', 'url', 'Sweaters & Cardigans', 18),
(rowid, 'Water Bottles', 'url', 'Denim', 18),
(rowid, 'Grooming & Skincare', 'url', 'Pants', 18),
(rowid, 'Product care', 'url', 'Shorts', 18),

(rowid, 'Featured', 'url', 'Featured', 26),

(rowid, 'New In', 'url', 'New In', 27),
(rowid, 'Best Sellers', 'url', 'Best Sellers', 27),
(rowid, 'Loungewear', 'url', 'Loungewear', 27),
(rowid, 'Good Denim', 'url', 'Good Denim', 27),
(rowid, 'Sale', 'url', 'Sale', 27),
(rowid, 'Gift Cards', 'url', 'Gift Cards', 27),

(rowid, 'Clothing', 'url', 'Clothing', 26),
(rowid, 'Shop All', 'url', 'Shop All', 28),
(rowid, 'Jackets & Outerwear', 'url', 'Jackets & Outerwear', 28),
(rowid, 'T-Shirts', 'url', 'T-Shirts', 28),
(rowid, 'Blouses', 'url', 'Blouses', 28),
(rowid, 'Sweatshirts & Sweaters', 'url', 'Sweatshirts & Sweaters', 28),
(rowid, 'Dresses & Skirts', 'url', 'Dresses & Skirts', 28),
(rowid, 'Denim', 'url', 'Denim', 28),
(rowid, 'Pants', 'url', 'Pants', 28),
(rowid, 'Shorts', 'url', 'Shorts', 28),
(rowid, 'Underwear', 'url', 'Underwear', 28),
(rowid, 'Activewear', 'url', 'Activewear', 28),

-- Women Accessories & Beauty
(rowid, 'Hats & Caps', 'url', 'Hats & Caps', 1),
(rowid, 'Shoes & Boots', 'url', 'Shop All', 18),
(rowid, 'Socks', 'url', 'Jackets & Outerwear', 18),
(rowid, 'Watches', 'url', 'T-Shirts', 18),
(rowid, 'Bags', 'url', 'Sweaters & Cardigans', 18),
(rowid, 'Water Bottles', 'url', 'Denim', 18),
(rowid, 'Beauty & Skincare', 'url', 'Beauty & Skincare', 18),
(rowid, 'Product care', 'url', 'Shorts', 18),
