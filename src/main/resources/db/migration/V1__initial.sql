create TABLE product (
    id bigint primary key,
    title text not null,
    available tinyint
    html_description text,
    vendor text,
    product_type text,
    handle text not null unique,
    updated_at text not null,
    created_at text not null,
    published_at text,
    tags json,
    options json,
);

create TABLE variant (
    id bigint primary key,
    product_id bigint not null,
    title text not null,
    price text,
    sku text,
    position int,
    compare_at_price text,
    fulfillment_service text,
    inventory_management text,
    options json,
    handle text not null unique,
    updated_at text not null,
    created_at text not null,
    published_at text,
    taxable tinyint,
    barcode json,
    grams int,
    image_id int,
    weight text,
    weight_unit text,
    tax_code text,
    requires_shipping tinyint
);

-- inventory

create TABLE image (
    id bigint primary key,
    product_id bigint not null,
    position int,
    updated_at text not null,
    created_at text not null,
    alt text,
    width int,
    height int,
    src text,
    variant_ids text
);

create TABLE collection (
    id bigint primary key,
   title text not null,
   parent_id bigint,
    position int,
    updated_at text not null,
    created_at text not null,
    hero json
);

create TABLE collection_product (
    collection_id bigint ,
    product_id bigint ,
);

