insert overwrite table temp.tmp_jjtj_t_user_hold_stock_cycle partition (init_date=?)

select cust_no uid,
	   ? start_time,
	   ?  end_time,
	   '1Y'  type,
	    round(avg(holdday),2) holdday_avg,
	    case when avg(holdday)<=90 then 0
	    when avg(holdday)<270 then 1
	    when avg(holdday)>=270 then 2
	    end hold_stock
from (
select cuid,cust_no,bdate,predate,occur_date,
	bseccode,
	edate,
	--datediff(to_date('2020-09-04'),from_unixtime(unix_timestamp(max(occur_date),'yyyymmdd'),'yyyy-mm-dd')) as diff
	datediff(to_date(edate),to_date(bdate)) as holdday
	from (
select a.cuid,a.cust_no,
c.bdate,
a.occur_date predate,
b.occur_date,
a.sec_code bseccode,
case when b.sec_code is null and a.sec_code is not null then a.occur_date end edate,
a.ranknum
from
(select cuid,cust_no,occur_date,
sec_code,
rank() over(partition by cust_no,sec_code order by etl_dt) ranknum,
cost_amount,
stock_balance,   ---持股数量
stock_asset,
source
from dccust.tb_fact_cust_stock_current
where occur_date between  ? and ?
and etl_dt between   ? and ?
--concat(cast((year('2020-05-22')-1) as string),substring('2020-05-22',5,6)) and '2020-05-22'
)a
left join
(
select cuid,cust_no,occur_date,
sec_code,
rank() over(partition by cust_no,sec_code order by etl_dt) ranknum,
cost_amount,
stock_balance,
stock_asset,
source
from dccust.tb_fact_cust_stock_current
where occur_date between  ? and ?
and etl_dt between  ? and ?
--concat(cast((year('2020-05-22')-1) as string),substring('2020-05-22',5,6)) and '2020-05-22'

)b
on a.cust_no=b.cust_no and a.sec_code=b.sec_code
and a.stock_balance=b.stock_balance and a.ranknum+1=b.ranknum
left join
(select   cuid,cust_no,occur_date bdate,
sec_code,stock_balance,cost_amount
from
(
select  cuid,cust_no,occur_date,
sec_code,stock_balance,cost_amount,
rank() over(partition by cust_no,sec_code,stock_balance,cost_amount order by etl_dt) ranknum
from dccust.tb_fact_cust_stock_current
)c
where ranknum=1
)c on a.cust_no=c.cust_no and a.sec_code=c.sec_code
and a.stock_balance=c.stock_balance and a.cost_amount=c.cost_amount
)a
 where edate is not null and bdate<edate and edate<> ?
 --edate<>date(now())-1
 )a
 --where  cust_no='30825780'
 group by cuid,cust_no