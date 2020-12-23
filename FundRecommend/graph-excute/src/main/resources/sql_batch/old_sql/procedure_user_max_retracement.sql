insert overwrite table temp.tmp_jjtj_t_user_max_retracement partition(init_date=20200911)
select cust_no uid,
       ? start_time,
	   ? end_time,
	   '1Y'  type,
	   max(falldown)  max_retracement
	   from (
select cust_no,
	   occur_date,
	   d_profit,
	   sum_profit,
	   profit_ratio,---截止日累计收益率
	   max_profit_rate,---截止日最大收益率
	   case when 1+max_profit_rate/100<>0 then
	   round((max_profit_rate-profit_ratio)/(1+max_profit_rate/100),4)
	   else 0
	   end falldown  ---回撤
from(
select cust_no,
	   occur_date,
	   d_profit,
	   sum_profit,
	   profit_ratio,---区间累计收益率
	   max(profit_ratio) over(partition by cust_no order by occur_date ) max_profit_rate
from (
select a.cust_no,a.occur_date,
	   d_profit,
	   sum_profit,--区间累计收益
	   case when nvl(begin_position,0)+nvl(begin_midway_asset,0)+nvl(begin_debt,0)+nvl(sum_asset_in,0)<>0 then
	   round(sum_profit/(nvl(begin_position,0)+nvl(begin_midway_asset,0)+nvl(begin_debt,0)+nvl(sum_asset_in,0))*100,4)
	   else 0
	   end profit_ratio ---区间累计收益率
	   from
(
select a1.cust_no,a1.occur_date,
	   avg(a1.d_profit) d_profit,---日收益
	   sum(a2.d_profit) sum_profit,--区间收益合计
	   sum(a2.d_asset_in) sum_asset_in,--区间关联流入合计
	   sum(a2.d_asset_out) sum_asset_out --区间关联流出合计
from (
select cust_no,occur_date,
	   d_profit,--当日收益合计
	   d_asset_in,--当日关联流入合计
	   d_asset_out --当日关联流出合计
from  temp.tmp_jjtj_t_user_profit_d
where init_date between ? and ?
--date'2019-12-08'and date'2020-05-08'
)a1
left join
(
select cust_no,occur_date,
	   d_profit,--当日收益合计
	   d_asset_in,--当日关联流入合计
	   d_asset_out --当日关联流出合计
from  temp.tmp_jjtj_t_user_profit_d
where init_date between ? and ?
--date'2019-12-08'and date'2020-05-08'
)a2 on a1.cust_no=a2.cust_no
where datediff(a1.occur_date,a2.occur_date)>=0
group by a1.cust_no,a1.occur_date
)a
left join
(select cust_no,occur_date,
	   d_begin_position  begin_position,--期初持仓市值b1
	   d_begin_midway_asset  begin_midway_asset,--期初关联在途资产b2
	   d_begin_debt  begin_debt  ---期初关联负债b3
	   from (
select cust_no,occur_date,
	   rank() over (partition by cust_no order by init_date ) ranknum,
	   d_begin_position,--当日期初持仓市值b1
	   d_begin_midway_asset,--当日期初关联在途资产b2
	   d_begin_debt, ---当日期初关联负债b3
	   d_asset_in,  --当日关联流入（正值）f1
	   d_asset_out, ---当日关联流出（正值）f2
	   d_profit,--当日收益净值 p=(e1-b1)+(e2-b2)-(e3-b3)-(f1-f2)
	   d_profit_rate ----当日收益率 pr=p/(b1+b2+b3+f1)
from  temp.tmp_jjtj_t_user_profit_d
where init_date between ? and ?
--date'2019-12-08'and date'2020-05-08'
)b
where ranknum=1
)b on a.cust_no=b.cust_no
--where  a.cust_no='00019230'
)a )a
)a
group by cust_no