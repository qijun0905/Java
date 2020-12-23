insert overwrite table temp.tmp_jjtj_t_user_profitability partition(init_date=?)

select a.cust_no uid,
	   ? start_time,
	   ? end_time,
	   '1Y'  type,
	   case when nvl(begin_position,0)+nvl(begin_midway_asset,0)+nvl(begin_debt,0)+nvl(sum_asset_in,0)<>0 then
	   round(sum_profit/(nvl(begin_position,0)+nvl(begin_midway_asset,0)+nvl(begin_debt,0)+nvl(sum_asset_in,0))*100,4)
	   else 0
	   end profit_ratio ---区间收益率
	   from
(select cust_no,occur_date,
	   d_begin_position  begin_position,--期初持仓市值b1
	   d_begin_midway_asset  begin_midway_asset,--期初关联在途资产b2
	   d_begin_debt  begin_debt  ---期初关联负债b3
	   from (
select cust_no,occur_date,
	   rank() over (partition by cust_no order by init_date ) ranknum,
	   current_position_asset,---期末持仓e1
	   current_midway_asset,--期末关联在途资产e2
	   current_debt, --期末关联负债e3
	   d_begin_position,--当日期初持仓市值b1
	   d_begin_midway_asset,--当日期初关联在途资产b2
	   d_begin_debt, ---当日期初关联负债b3
	   d_asset_in,  --当日关联流入（正值）f1
	   d_asset_out, ---当日关联流出（正值）f2
	   d_profit,--当日收益净值 p=(e1-b1)+(e2-b2)-(e3-b3)-(f1-f2)
	   d_profit_rate----当日收益率 pr=p/(b1+b2+b3+f1)
from  dctmp.tmp_jjtj_t_user_profit_d
where init_date between ? and ?
--date'2019-12-08'and date'2020-05-08'
)a
where ranknum=1
)a
left join
(
select cust_no,
	   sum(d_profit) sum_profit,--区间收益合计
	   sum(d_asset_in) sum_asset_in,--区间关联流入合计
	   sum(d_asset_out) sum_asset_out --区间关联流出合计
from  dctmp.tmp_jjtj_t_user_profit_d
where init_date between ? and ?
--date'2019-12-08'and date'2020-05-08'
group by cust_no
)b on a.cust_no=b.cust_no