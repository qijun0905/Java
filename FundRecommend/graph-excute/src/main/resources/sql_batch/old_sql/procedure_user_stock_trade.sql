insert overwrite table temp.tmp_jjtj_t_user_stock_trade partition(init_date = ?)
select cust_no
,1 as has_trade
from dccust.tb_fact_cust_stock_current
where occur_date >= ?
and occur_date <= ?
and etl_dt >= ?
--date_sub(from_unixtime(unix_timestamp(cast('20200402' as string),'yyyymmmdd') ,'yyyy-mm-dd'),365)
and etl_dt <= ?