CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    available INTEGER
    html_description TEXT,
    vendor TEXT,
    product_type TEXT,
    handle TEXT NOT NULL UNIQUE,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    published_at TEXT,
    tags JSON,
    options JSON
);

CREATE TABLE IF NOT EXISTS variant (
    id INTEGER PRIMARY KEY,
    product_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    price TEXT,
    sku TEXT,
    position INTEGER,
    compare_at_price TEXT,
    fulfillment_service TEXT,
    inventory_management TEXT,
    options JSON,
    handle TEXT NOT NULL UNIQUE,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    published_at TEXT,
    taxable INTEGER,
    barcode JSON,
    grams INTEGER,
    image_id INTEGER,
    weight TEXT,
    weight_unit TEXT,
    tax_code TEXT,
    requires_shipping INTEGER
);

-- inventory

CREATE TABLE IF NOT EXISTS image (
    id INTEGER PRIMARY KEY,
    product_id INTEGER NOT NULL,
    position INTEGER,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    alt TEXT,
    width INTEGER,
    height INTEGER,
    src TEXT,
    variant_ids TEXT
);

CREATE TABLE IF NOT EXISTS collection (
    id INTEGER PRIMARY KEY,
   title TEXT NOT NULL,
   parent_id INTEGER,
    position INTEGER,
    updated_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    hero JSON
);

CREATE TABLE IF NOT EXISTS collection_product (
    collection_id INTEGER,
    product_id INTEGER
);

