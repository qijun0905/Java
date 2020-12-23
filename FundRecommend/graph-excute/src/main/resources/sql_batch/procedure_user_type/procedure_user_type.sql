insert overwrite TABLE  temp.tmp_jjtj_t_user_type partition(init_date = ?)

select
t.cust_no
,case when sum(t.is_fund_user)>2 then 1
    else  sum(t.is_fund_user)
    end as is_fund_user
,sum(t.is_stock_user) as is_stock_user
,sum(t.is_new_user) as is_new_user
from (

select
cust_no
, 1 as is_fund_user
, 0 as is_stock_user
, 0 as is_new_user
from dccust.tb_fact_cust_ofs_stock
where stock_asset > 10
and trim(fund_code)<>'863001'
and occur_date between ? and ?
and etl_dt between ? and ?
group by cust_no

union all

select
cust_no
, 2 as is_fund_user
, 0 as is_stock_user
, 0 as is_new_user
from dccust.tb_fact_cust_ofs_stock
where stock_asset > 10
and trim(fund_code)<>'863001'
and occur_date between ? and ?
and etl_dt between ? and ?
group by cust_no

union all

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
from  sjzxebs.tb_fact_cust_info a
where open_date <=  ?
and cust_status='0'
and is_org='0'

) t2 on trim(t1.cust_no) = trim(t2.cust_no)

group by t1.cust_no

union all

select
cust_no
, 0 as is_fund_user
, 0 as is_stock_user
, 1 as is_new_user
from sjzxebs.tb_fact_cust_info where  length(trim(close_date)) = 0
and open_date  >  ?
and cust_status='0'
and is_org='0'

union all

select
cust_no
, 0 as is_fund_user
, 0 as is_stock_user
, 0 as is_new_user
from sjzxebs.tb_fact_cust_info where  length(trim(close_date)) = 0
and cust_status='0'
and is_org='0'
) t group by t.cust_no