-- ----------------------------
-- Add column makeup_interval to report_category_template table
-- ----------------------------
drop procedure if exists Add_Column_Makeup_Interval_To_Report_Category_Template;

DELIMITER $$
CREATE PROCEDURE Add_Column_Makeup_Interval_To_Report_Category_Template()
BEGIN
    DECLARE _count INT;
    SET _count = (  SELECT COUNT(*) 
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = 'steel_linux' AND
                    	TABLE_NAME = 'report_category_template' AND 
                            COLUMN_NAME = 'makeup_interval');
    IF _count = 0 THEN
        ALTER TABLE steel_linux.report_category_template
            ADD COLUMN makeup_interval INT NOT NULL DEFAULT 300;
    END IF;
END $$
DELIMITER ;

call Add_Column_Makeup_Interval_To_Report_Category_Template();


-- ----------------------------
-- Add column edit_status to report_category_template table
-- ----------------------------
drop procedure if exists Add_Column_Edit_Status_To_Report_Index;

DELIMITER $$
CREATE PROCEDURE Add_Column_Edit_Status_To_Report_Index()
BEGIN
    DECLARE _count INT;
    SET _count = (  SELECT COUNT(*) 
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = 'steel_linux' AND 
                        TABLE_NAME = 'report_index' AND 
                        COLUMN_NAME = 'edit_status');
    IF _count = 0 THEN
        ALTER TABLE steel_linux.report_index
            ADD COLUMN edit_status INT NOT NULL DEFAULT 0;
    END IF;
END $$
DELIMITER ;

call Add_Column_Edit_Status_To_Report_Index()


-- ----------------------------
-- Add column cron_setting_method to report_category_template table
-- ----------------------------
drop procedure if exists Add_Column_Cron_Setting_Method_To_Report_Category_Template;

DELIMITER $$
CREATE PROCEDURE Add_Column_Cron_Setting_Method_To_Report_Category_Template()
BEGIN
    DECLARE _count INT;
    SET _count = (  SELECT COUNT(*) 
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = 'steel_linux' AND
                    	TABLE_NAME = 'report_category_template' AND 
                            COLUMN_NAME = 'cron_setting_method');
    IF _count = 0 THEN
        ALTER TABLE steel_linux.report_category_template
            ADD COLUMN cron_setting_method INT NOT NULL DEFAULT 0;
    END IF;
END $$
DELIMITER ;

call Add_Column_Cron_Setting_Method_To_Report_Category_Template();