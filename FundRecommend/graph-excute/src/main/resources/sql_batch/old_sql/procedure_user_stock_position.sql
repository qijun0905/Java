insert overwrite table temp.tmp_jjtj_t_user_stock_position partition(init_date)

select cust_no uid,
	   start_time,
	   end_time,
	   '1W'  type,
	   case when sum(asset)<>0 then round(sum(asset*stk_position)/sum(asset) ,4)
	   else 0
	   end stk_position, ---加权平均仓位
	   max(stk_position)  max_stk_position, --最高仓位
	   min(stk_position)  min_stk_position, --最低仓位
	   end_time init_date
	    from
(select cust_no,occur_date,
 	   dayofweek(occur_date) weeknum,
	   --extract(dow from occur_date) weeknum,
	   weekofyear(occur_date) week_id,
	   --to_char(occur_date,'iw') week_id,
	   year(occur_date) year_id,
	   --to_char(occur_date,'yyyy') year_id,
	   stk_gp_jz+stk_gz_jz+stk_hgt+stk_sgt+stk_rz+amend_amt stk_mv, --股票市值
	   asset_jz+asset_rz  asset,--资产合计
	   case when asset_jz+asset_rz<>0 then
	   round((stk_gp_jz+stk_gz_jz+stk_hgt+stk_sgt+stk_rz+amend_amt)/(asset_jz+asset_rz)*100,2)
	   else 0
	   end stk_position, --仓位
	   fnd_jz,--资金余额
	   stk_gp_jz,
	   stk_gz_jz,
	   ofs_asset,
	   stk_hgt,
	   stk_sgt,
	   --amend_amt_jz,
	   asset_jz,  --总资产
	   fnd_rz,
	   stk_rz,
	   amend_amt,
	   asset_rz
from dccust.tb_fact_cust_attr
where etl_dt between ? and ?
)a
left join
(
select year,week_id,
min(occur_date) start_time,
max(occur_date) end_time
from (
select last_occur_date,
	   next_occur_date,
	   occur_date occurdate,
	   from_unixtime(unix_timestamp(occur_date,'yyyymmdd'),'yyyy-mm-dd') occur_date,
	   year,
	   dayofweek(from_unixtime(unix_timestamp(occur_date,'yyyymmdd'),'yyyy-mm-dd')) weeknum,
	   --extract(dow from to_date(occur_date,'yyyymmdd')) weeknum,
	   weekofyear(from_unixtime(unix_timestamp(occur_date,'yyyymmdd'),'yyyy-mm-dd')) week_id
	   --to_char(to_date(occur_date,'yyyymmdd'),'iw') week_id
from dccust.tb_dim_occur_date
where etl_dt between ? and ?
and year>2019
)b1 group by year,week_id
)b on a.week_id=b.week_id and a.year_id=b.year
--where cust_no='40303667'
group by cust_no,start_time,end_time