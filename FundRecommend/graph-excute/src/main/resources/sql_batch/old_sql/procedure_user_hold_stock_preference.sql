insert overwrite table temp.tmp_jjtj_t_user_hold_stock_preference partition(init_date = ?)
select t3.cust_no,t3.sec_code
, 3 * (1 + t3.ratio) * (1 - t3.diff / 100.0) as score
from (
select t1.cust_no,t1.sec_code,t1.diff,
case
when sum(t2.stock_asset) over(partition by t1.cust_no) is null or sum(t2.stock_asset) over(partition by t1.cust_no) = 0.0  then 0.0
when sum(t2.stock_asset) over(partition by t1.cust_no) is not null
	then nvl(t2.stock_asset,0.0) / sum(t2.stock_asset) over(partition by t1.cust_no)
end ratio
from (
select
cust_no
,sec_code
, datediff(to_date(?),max(occur_date)) as diff
from dccust.tb_fact_cust_stock_current
where sec_type  in('66','67')
and occur_date >= ?
and occur_date <= ?
and etl_dt >= ?
and etl_dt <= ?
group by cust_no,sec_code
) t1 left join (
select
cust_no
,sec_code
,stock_asset
from dccust.tb_fact_cust_stock_current
where --sec_type  in('66','67','61') and
 occur_date = ? and etl_dt = ?
) t2 on t1.cust_no = t2.cust_no and t1.sec_code = t2.sec_code
 )t3