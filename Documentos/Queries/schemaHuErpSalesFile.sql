CREATE TABLE `hu_erp_2017_24_sales_invoices` (
  `arbiter_key` int(11) NOT NULL,
  `row_key` char(32) NOT NULL DEFAULT '',
  `import_id` int(11) NOT NULL,
  `tax_entity` varchar(255) DEFAULT NULL,
  `tax_id` varchar(255) DEFAULT NULL,
  `supplier_name` varchar(255) DEFAULT NULL,
  `supplier_vat_number` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country_code_1` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `street_name` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `building` varchar(255) DEFAULT NULL,
  `floor` varchar(255) DEFAULT NULL,
  `door` varchar(255) DEFAULT NULL,
  `customer_id` varchar(255) DEFAULT NULL,
  `country_code_2` varchar(255) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `taxpayer_id_2` varchar(255) DEFAULT NULL,
  `taxpayer_id_3` varchar(255) DEFAULT NULL,
  `taxpayer_id_4` varchar(255) DEFAULT NULL,
  `taxpayer_id_1` varchar(255) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `invoice_issue_date` date DEFAULT NULL,
  `period` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  `exchange_rate` varchar(255) DEFAULT NULL,
  `taxpayer_id` varchar(255) DEFAULT NULL,
  `invoice_delivery_date` date DEFAULT NULL,
  `county_code` varchar(255) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `invoice_accounting_delivery_date` date DEFAULT NULL,
  `invoice_delivery_period_start` date DEFAULT NULL,
  `invoice_delivery_period_end` date DEFAULT NULL,
  `electronic_invoice_hash` varchar(255) DEFAULT NULL,
  `document_net_amount` decimal(20,2) DEFAULT NULL,
  `tax_base_amount` decimal(20,2) DEFAULT NULL,
  `tax_amount` decimal(20,2) DEFAULT NULL,
  `discount_base_amount` decimal(20,2) DEFAULT NULL,
  `discount_percentage_temp` varchar(255) DEFAULT NULL,
  `discount_percentage` decimal(20,2) DEFAULT NULL,
  `vat_amount` decimal(20,2) DEFAULT NULL,
  `line_net_amount` decimal(20,2) DEFAULT NULL,
  `gross_amount` decimal(20,2) DEFAULT NULL,
  `payment_date` date DEFAULT NULL,
  `invoice_destination_country` varchar(255) DEFAULT NULL,
  `number_of_lines` varchar(255) DEFAULT NULL,
  `merge_key` varchar(255) NOT NULL DEFAULT '',
  `final_version` tinyint(1) NOT NULL DEFAULT '1',
  `customer_county_code` varchar(255) DEFAULT NULL,
  `original_document` varchar(255) DEFAULT NULL,
  `supplier_vat_number_1` varchar(255) DEFAULT NULL,
  `customer_building` varchar(50) DEFAULT NULL,
  `supplier_vat_number_temp` varchar(255) DEFAULT NULL,
  `invoice_type` varchar(255) DEFAULT NULL,
  `customer_postal_code` varchar(255) DEFAULT NULL,
  `customer_street_name` varchar(50) DEFAULT NULL,
  `customer_number` varchar(50) DEFAULT NULL,
  `customer_floor` varchar(50) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `original_document_temp` varchar(255) DEFAULT NULL,
  `customer_door` varchar(50) DEFAULT NULL,
  `document_country_code` varchar(255) DEFAULT NULL,
  `customer_city` varchar(255) DEFAULT NULL,
  `customer_region` varchar(50) DEFAULT NULL,
  `customer_country_code` varchar(255) DEFAULT NULL,
  `invoice_number_temp` varchar(255) DEFAULT NULL,
  `last_modification_document` varchar(255) DEFAULT NULL,
  `tcode` varchar(255) DEFAULT NULL,
  `last_modification_document_temp` varchar(255) DEFAULT NULL,
  KEY `row_key` (`row_key`) USING BTREE,
  KEY `tax_entity` (`tax_entity`) USING BTREE,
  KEY `final_version` (`final_version`) USING BTREE,
  KEY `import_id` (`import_id`) USING BTREE,
  KEY `merge_key` (`merge_key`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `hu_erp_2017_24_sales_invoice_lines` (
  `arbiter_key` int(11) NOT NULL,
  `row_key` char(32) NOT NULL DEFAULT '',
  `hu_erp_sales_invoice_id` varchar(255) DEFAULT NULL,
  `import_id` int(11) NOT NULL,
  `tax_entity` varchar(255) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `line_number_temp` varchar(255) DEFAULT NULL,
  `line_number` varchar(255) DEFAULT NULL,
  `product_code_value_temp` varchar(255) DEFAULT NULL,
  `product_code_value` varchar(255) DEFAULT NULL,
  `quantity` decimal(20,2) DEFAULT NULL,
  `advance_indicator` tinyint(1) DEFAULT NULL,
  `unit_of_measure` varchar(50) DEFAULT NULL,
  `unit_price_1` decimal(20,3) DEFAULT NULL,
  `line_net_amount_1` decimal(20,2) DEFAULT NULL,
  `line_tax_percentage` decimal(20,2) DEFAULT NULL,
  `line_tax_base_amount` decimal(20,2) DEFAULT NULL,
  `line_tax_amount` decimal(20,2) DEFAULT NULL,
  `line_tax_code` varchar(255) DEFAULT NULL,
  `line_discount_percentage_temp` varchar(255) DEFAULT NULL,
  `line_discount_percentage` decimal(20,2) DEFAULT NULL,
  `line_discount_base_amount` decimal(20,2) DEFAULT NULL,
  `line_discount_amount` decimal(20,2) DEFAULT NULL,
  `line_description` varchar(255) DEFAULT NULL,
  `line_delivery_date` date DEFAULT NULL,
  `product_fee_paper_rate` decimal(20,2) DEFAULT NULL,
  `product_fee_paper_base` decimal(20,2) DEFAULT NULL,
  `product_fee_paper_value` decimal(20,2) DEFAULT NULL,
  `product_fee_paper_tax_code` varchar(255) DEFAULT NULL,
  `product_fee_metal_rate` decimal(20,2) DEFAULT NULL,
  `product_fee_metal_base` decimal(20,2) DEFAULT NULL,
  `product_fee_metal_value` decimal(20,2) DEFAULT NULL,
  `product_fee_metal_tax_code` varchar(255) DEFAULT NULL,
  `product_fee_wood_rate` decimal(20,2) DEFAULT NULL,
  `product_fee_wood_base` decimal(20,2) DEFAULT NULL,
  `product_fee_wood_value` decimal(20,2) DEFAULT NULL,
  `product_fee_wood_tax_code` varchar(255) DEFAULT NULL,
  `product_fee_plastic_rate` decimal(20,2) DEFAULT NULL,
  `product_fee_plastic_base` decimal(20,2) DEFAULT NULL,
  `product_fee_plastic_value` decimal(20,2) DEFAULT NULL,
  `product_fee_plastic_tax_code` varchar(255) DEFAULT NULL,
  `line_net_amount_2` decimal(20,2) DEFAULT NULL,
  `unit_price_2` decimal(20,2) DEFAULT NULL,
  `line_net_amount` decimal(20,2) DEFAULT NULL,
  `unit_price` decimal(20,2) DEFAULT NULL,
  `gross_amount` decimal(20,2) DEFAULT NULL,
  `merge_key` varchar(255) NOT NULL DEFAULT '',
  `final_version` tinyint(1) NOT NULL DEFAULT '1',
  `original_item_temp` varchar(255) DEFAULT NULL,
  `product_type` varchar(255) DEFAULT NULL,
  `original_item` varchar(255) DEFAULT NULL,
  `invoice_number_temp` varchar(255) DEFAULT NULL,
  KEY `row_key` (`row_key`) USING BTREE,
  KEY `tax_entity` (`tax_entity`) USING BTREE,
  KEY `final_version` (`final_version`) USING BTREE,
  KEY `import_id` (`import_id`) USING BTREE,
  KEY `merge_key` (`merge_key`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;