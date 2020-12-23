insert overwrite table temp.tmp_jjtj_t_user_stock_trade partition(init_date = ? )
select cust_no
,1 as has_trade
from dccust.tb_fact_cust_stock_current
where occur_date >= ?
and occur_date <= ?
and etl_dt >= ?
and etl_dt <= ?