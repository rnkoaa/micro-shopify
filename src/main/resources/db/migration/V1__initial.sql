PRAGMA foreign_keys = ON;

create TABLE IF NOT EXISTS category (
  id INTEGER PRIMARY KEY,
  title TEXT NOT NULL,
  parent_id INTEGER,
  handle TEXT NOT null,
  position INTEGER,
  description TEXT,
  updated_at TEXT NOT NULL,
  created_at TEXT NOT NULL,
  hero JSON,
  sort_options JSON,
  default_filter_groups JSON,
  UNIQUE(handle)
);

-- products
create TABLE IF NOT EXISTS product (
  id INTEGER PRIMARY KEY,
  title TEXT NOT NULL,
  available INTEGER,
  html_description TEXT,
  vendor TEXT,
  product_type TEXT,
  featured_image JSON,
  swatch_color TEXT,
  swatch_color_name TEXT,
  price TEXT,
  option_names JSON,
  options JSON,
  handle TEXT NOT NULL UNIQUE,
  updated_at TEXT NOT NULL,
  created_at TEXT NOT NULL,
  published_at TEXT,
  tags JSON,
  UNIQUE(handle)
);

create TABLE IF NOT EXISTS category_product (
    category_id INTEGER NOT NULL ,
    product_id INTEGER NOT NULL ,
    PRIMARY KEY (category_id, product_id),
    FOREIGN KEY (category_id) REFERENCES category (id)
--            ON DELETE CASCADE ON UPDATE NO ACTION,
    FOREIGN KEY (product_id) REFERENCES product (id)
--            ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS variant (
    id INTEGER NOT NULL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    price TEXT,
    sku TEXT,
    position INTEGER,
    compare_at_price TEXT,
    fulfillment_service TEXT,
    inventory_management TEXT,
    options json,
    handle text NOT NULL UNIQUE,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    published_at TEXT,
    taxable TINYINT,
    barcode JSON,
    grams INTEGER,
    image_id INTEGER,
    weight TEXT,
    weight_unit TEXT,
    tax_code TEXT,
    requires_shipping INTEGER,
    UNIQUE(product_id, title),
    FOREIGN KEY (product_id) REFERENCES product (id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS product_inventory (
    id INTEGER NOT NULL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    variant_id INTEGER NOT NULL,
    quantity INTEGER,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product (id)
      ON DELETE CASCADE ON UPDATE NO ACTION,
    FOREIGN KEY (variant_id) REFERENCES variant (id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS image (
    id INTEGER NOT NULL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    position INTEGER,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    alt TEXT,
    width INTEGER,
    height INTEGER,
    src TEXT,
    variant_ids TEXT,
    FOREIGN KEY (product_id) REFERENCES product (id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);

