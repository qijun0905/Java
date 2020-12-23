insert overwrite table temp.tmp_jjtj_t_user_hold_fund_preference partition(init_date = ?)

select t3.cust_no,t3.fund_code
,  t3.ratio * (1 - t3.diff / 100.0) as score
from (

select t1.cust_no,t1.fund_code,t1.diff,
case
when sum(t2.stock_asset) over(partition by t1.cust_no) is null or sum(t2.stock_asset) over(partition by t1.cust_no) = 0.0  then 0.0
when sum(t2.stock_asset) over(partition by t1.cust_no) is not null
	then nvl(t2.stock_asset,0.0) / sum(t2.stock_asset) over(partition by t1.cust_no)
end ratio

from (
select
cust_no
,fund_code
, datediff(to_date(?),max(occur_date)) as diff
from dccust.tb_fact_cust_ofs_stock
where occur_date >= to_date(?)
and occur_date <= to_date(?)
and etl_dt >= ?
and etl_dt <= ?
and trim(fund_code)<>'863001'
group by cust_no,fund_code
) t1 left join (
select
cust_no
,fund_code
,stock_asset
from dccust.tb_fact_cust_ofs_stock
where  occur_date = ?  and etl_dt = ?
) t2 on t1.cust_no = t2.cust_no and t1.fund_code = t2.fund_code

) t3