
insert overwrite table temp.tmp_jjtj_t_user_profit_d partition(init_date)

select cust_no,occur_date,
	   --to_date(occur_date,'yyyymmdd') occur_date,
	   sum(current_position_asset) current_position_asset ,---期末持仓e1
	   sum(current_midway_asset)current_midway_asset, --select  from dctmp.tmp_jjtj_t_user_profit_d limit 10;期末关联在途资产e2
	   sum(current_debt) current_debt,  --期末关联负债e3
	   sum(d_begin_position) d_begin_position,  ----当日期初持仓市值b1
	   sum(d_begin_midway_asset) d_begin_midway_asset, ----当日期初关联在途资产b2
	   sum(d_begin_debt) d_begin_debt, ---当日期初关联负债b3
	   sum(d_asset_in) d_asset_in, --当日关联流入（正值）f1
	   sum(d_asset_out) d_asset_out, -----当日关联流出（正值）f2
	   sum(d_profit) d_profit, --当日收益净值 p=(e1-b1)+(e2-b2)-(e3-b3)-(f1-f2)
	   case when sum(d_begin_position)+sum(d_begin_midway_asset)+sum(d_begin_debt)+sum(d_asset_in)0 then
	   round(sum(d_profit)(sum(d_begin_position)+sum(d_begin_midway_asset)+sum(d_begin_debt)+sum(d_asset_in))100,4)
	   else 0
	   end d_profit_rate,
	   etl_dt as init_date
from (
select cust_no,
       from_unixtime(unix_timestamp(cast(occur_date as string),'yyyymmdd'),'yyyy-mm-dd') occur_date,
       etl_dt,
	   current_position_asset,---期末持仓e1
	   current_midway_asset,--期末关联在途资产e2
	   current_debt, --期末关联负债e3
	   d_begin_position,--当日期初持仓市值b1
	   d_begin_midway_asset,--当日期初关联在途资产b2
	   d_begin_debt, ---当日期初关联负债b3
	   d_asset_in,  --当日关联流入（正值）f1
	   d_asset_out, ---当日关联流出（正值）f2
	   d_profit,--当日收益净值 p=(e1-b1)+(e2-b2)-(e3-b3)-(f1-f2)
	   d_profit_rate----当日收益率 pr=p(b1+b2+b3+f1)
from dccust.tb_fact_cust_profit_sec
where etl_dt between ? and ?
)a
 --where  cust_no = '20921032'
group by cust_no,occur_date,etl_dt