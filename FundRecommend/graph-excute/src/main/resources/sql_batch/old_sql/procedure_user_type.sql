insert overwrite TABLE  temp.tmp_jjtj_t_user_type partition(init_date = ?)

select
t.cust_no
,sum(t.is_fund_user) as is_fund_user
,sum(t.is_stock_user) as is_stock_user
,sum(t.is_new_user) as is_new_user
from (
-- fund_user
select
cust_no
, 1 as is_fund_user
, 0 as is_stock_user
, 0 as is_new_user
from dccust.tb_fact_cust_ofs_trd_detail
where app_amt > 1
and fund_code<>'863001'
and occur_date < ? and etl_dt < ?
group by cust_no


union all

-- stock_user
select
t1.cust_no
, 0 as is_fund_user
, 1 as is_stock_user
, 0 as is_new_user from
(
select
uid as  cust_no
from temp.tmp_jjtj_t_user_stock_trade
where init_date = ?

) t1
left semi join
(
select cust_no
from dccust.tb_fact_cust_info a
where open_date <=  ?
and cust_status='0'
and is_org='0'
and etl_dt = ?

) t2 on trim(t1.cust_no) = trim(t2.cust_no)
-- login


group by t1.cust_no


union all

-- all_user
select
cust_no
, 0 as is_fund_user
, 0 as is_stock_user
, 1 as is_new_user
from dccust.tb_fact_cust_info where  length(trim(close_date)) = 0
and open_date  > ?
and etl_dt = ?
and cust_status='0'
and is_org='0'

union all

-- all_user
select
cust_no
, 0 as is_fund_user
, 0 as is_stock_user
, 0 as is_new_user
from dccust.tb_fact_cust_info where  length(trim(close_date)) = 0
and etl_dt =?
and cust_status='0'
and is_org='0'
) t group by t.cust_no