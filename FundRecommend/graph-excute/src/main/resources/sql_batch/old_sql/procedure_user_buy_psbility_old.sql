
insert overwrite table temp.tmp_jjtj_t_user_buy_psbility_old partition (init_date=?)
select a.cust_no uid,nvl(eduscore,0)+nvl(pfscore,0)+nvl(tagescore,0)+nvl(hfscore,0)+nvl(rds,0)+nvl(nfs,0)
        +nvl(assets,0)+nvl(bnks,0)+nvl(ps,0)+nvl(rs,0)+nvl(fs,0)+nvl(hs,0)+nvl(risks,0) score
	 ,eduscore,pfscore,tagescore,hfscore,nfs,rds,assets,bnks,ps,rs,fs,hs,risks
from (
select cust_no,open_date,gender,age,
	education_level,profession,
	case when   education_level='硕士' then 0.4
	when  education_level in ('博士','学士','大专') then 0.2
	when  education_level in ('高中','中专','初中及以下') then 0.1
	else 0
	end eduscore
	,case when  profession in ('29','10','08') then 1
	    when  profession in ('16','12','27','14','24','32','13') then 0.4
	    when  profession in ('11','23','17','01') then 0.3
	    when  profession in('15','98','28','02') then 0.2
	    when  profession in('03','05','04','06','99','09','07') then 0.1
	    else 0
	    end pfscore
	,datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd')) tradeday
	,case when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=365*3 then 0.1
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=365 then 0.3
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=90 then 0.4
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=30 then 1
		else 1.5
		end tagescore
from dccust.tb_fact_cust_info
where cust_status='0'
and is_org='0'
and etl_dt=?
and max_net_asset>0
)a
join
(
select uid from temp.tmp_jjtj_t_user_type
 where init_date=?
 and (is_fund_user='1'
    or  is_stock_user='1'
    or  is_new_user='1')
)b on a.cust_no=b.uid
left join
(
select t.cust_no,t.stimes,a.cust_no user_3m,a.times,
case when times>=10 then 3
    when times>=5 then 2.5
    when times>=2 then 2
    when times=1 then 1.5
    else 1.2
    end hfscore
    from
(select cust_no, count(*) stimes
from dccust.tb_fact_cust_ofs_trd_detail
where etl_dt <= ?
and occur_date <=?
and trim(fund_code)<>'863001'
and trim(business_code) in ('122')
and app_amt>10
group by cust_no
)t
left join
(select cust_no, count(*) times
from dccust.tb_fact_cust_ofs_trd_detail
where etl_dt between ? and ?
and occur_date between ? and ?
and trim(fund_code)<>'863001'
and trim(business_code) in ('122')
and app_amt>10
group by cust_no
)a on t.cust_no=a.cust_no
)c on a.cust_no=c.cust_no
left join
(
select a.cust_no,a.times,
case when times>=10 then 1.3
    when times>=5 then 0.6
    when times>=2 then 0.5
    else 0.3
    end nfs
    from
(select cust_no, count(*) times
from dccust.tb_fact_cust_ofs_trd_detail
where etl_dt<= ?
and occur_date <=?
and trim(fund_code)<>'863001'
and trim(business_code) in ('120')
and app_amt>10
group by cust_no
)a
left join
(
select   cust_no
from dccust.tb_fact_cust_ofs_trd_detail
where trim(fund_code)<>'863001'
and trim(business_code) = '122'
and occur_date<=?
and etl_dt<=?
group by cust_no
)a1 on a.cust_no=a1.cust_no
where a1.cust_no is null
)t on a.cust_no=t.cust_no
left join
(
 select distinct cust_no, 1.5 rds
from dccust.tb_fact_cust_ofs_trd_detail
where occur_date between ? and ?
and trim(fund_code)<>'863001'
and trim(business_code) in ('124','142')
and etl_dt between ? and ?
--and cust_no='00001896'
)d on a.cust_no=d.cust_no
left join
(
select uid,stock_position,
    case when stock_position>=70 then 0.1
        when stock_position<70 and stock_position>=40 then 0.3
        when stock_position<40 and stock_position>0 then 0.5
        else 0.3
        end ps
    from temp.tmp_jjtj_t_user_stock_position
    where init_date = ?
 --   and cust_no='00001896'
)e1 on a.cust_no=e1.uid
left join
(
select cust_no,evaluate_value,
    case when evaluate_value='C5' then 0.3
        when evaluate_value='C4' then 0.1
        when evaluate_value='C3' then 0.1
        else 0
        end risks
from dccust.tb_fact_cust_label_risk_prop
where etl_dt= ?
)f on a.cust_no=f.cust_no
left join
(
select cust_no,
	   asset_jz+net_asset_rz  net_asset,  --净资产
	   case when asset_jz+net_asset_rz>=1000000 then 0.4
	   		when asset_jz+net_asset_rz>=500000 then 0.3
	   		when asset_jz+net_asset_rz>=200000 then 0.3
	   		when asset_jz+net_asset_rz>=50000 then 0.2
	   		when asset_jz+net_asset_rz>=10000 then 0.2
	   		when asset_jz+net_asset_rz>=0 then 0.1
	   		end assets
from dccust.tb_fact_cust_attr
where occur_date = date ?
and etl_dt= ?
)g on a.cust_no=g.cust_no
left join
(
 select cust_no,sum(occur_bal) bnk_in,
	   case when sum(occur_bal)>=500000 then 1.2
	   		when sum(occur_bal)>=200000 then 1
	   		when sum(occur_bal)>0 then 0.4
	   		when sum(occur_bal)=0 then 0.0
	   		end bnks
from(
select trim(cust_no) cust_no,request_date, occur_bal,etl_dt
from dcods.jzjyh_his_bnk_trf_ntfc
where request_type='01'
and trim(return_info)='交易成功'
and request_date between ? and ?
and etl_dt between ? and ?
)h1
group by cust_no
)h on a.cust_no=h.cust_no
left join
(
select uid,end_time,profit_ratio,
	    case when profit_ratio>=40 then 0.1
	    when profit_ratio>=20 then 0.2
	    when profit_ratio>=-10 then 0.3
	    else 0
	    end rs
from temp.tmp_jjtj_t_user_profitability
where init_date=?
)i on a.cust_no=i.uid
left join
(
select uid,max_retracement,
    case
        when max_retracement>=20 then 0.1
        when max_retracement>=10 then 0.2
        when max_retracement<10  and max_retracement>0 then 0.3
        else 0.2
        end fs
    from temp.tmp_jjtj_t_user_max_retracement
    where init_date =?
)j on a.cust_no=j.uid
left join
(
select uid,holdday_avg,
    case when holdday_avg>=180 then 0.1
        else 0.2
        end hs
    from temp.tmp_jjtj_t_user_hold_stock_cycle
    where init_date = ?
)k on a.cust_no=k.uid