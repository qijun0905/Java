insert overwrite table temp.tmp_jjtj_t_user_buy_psbility partition (init_date=?)
select a.cust_no uid,nvl(eduscore,0)+nvl(pfscore,0)+nvl(tagescore,0)+nvl(hfscore,0)+nvl(rds,0)
        +nvl(assets,0)+nvl(bnks,0)+nvl(ps,0)+nvl(rs,0)+nvl(fs,0)+nvl(hs,0)+nvl(risks,0) score
	 ,eduscore,pfscore,tagescore,hfscore,rds,assets,bnks,ps,rs,fs,hs,risks
from (
select cust_no,open_date,gender,age,
	education_level,profession,
	case when   education_level='硕士' then 0.4
	when  education_level='学士' then 0.3
	when  education_level='大专' then 0.2
	when  education_level in ('博士','大专','高中','中专','初中及以下') then 0.1
	else 0
	end eduscore
	,case when  profession ='10' then 1
	    when  profession ='17' then 0.7
	    when  profession ='28' then 0.5
	    when  profession in('29','11','24','98') then 0.3
	    when  profession in('16','15') then 0.3
	    when  profession in('12','01','14','08','99','09') then 0.2
	    when  profession in('23','02','03','04','05','06','07') then 0.1
	    else 0
	    end pfscore
	,datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd')) tradeday
	,case when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=365*3 then 0.1
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=365 then 0.2
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=90 then 0.6
		when datediff(?,from_unixtime(unix_timestamp(cast(open_date as string),'yyyymmdd'),'yyyy-mm-dd'))>=30 then 0.7
		else 0.9
		end tagescore
from dccust.tb_fact_cust_info
where cust_status='0'
and is_org='0'
and etl_dt= ?
and max_net_asset>0
)a
left join
(
select cust_no,
       case when fundtype like '%权益类%'or fundtype like '%QDII基金%' or fundtype like '%创新封闭式基金%'then 1.8
        else 0.6
        end hfscore
        from (
select cust_no,
    concat_ws(',',collect_set(fundtype)) fundtype
    from
(select cust_no,occur_date,
	   cast(fund_code as string) fund_code,
	   stock_balance, --基金数量
	   stock_asset--基金市值
from dccust.tb_fact_cust_ofs_stock
where trim(fund_code)<>'863001'
and stock_balance>10
and occur_date<= ?
and etl_dt<=?
)c1
 join
(
select cast(code as string)code,tag_value,
       case when tag_value in ('偏股混合型基金','增强指数型基金','平衡混合型基金','普通股票型基金','被动指数型基金') then '权益类'
         when tag_value in ('中短期理财债券型','债券型基金') then '固收类'
         else  tag_value
         end fundtype,
       weight,init_date
from temp.tmp_jjtj_t_fund_stock_tags
where init_date= ? and
tag_type='dfType'
and species_type='fund'
)c2 on trim(c1.fund_code)=trim(c2.code)
group by cust_no --,occur_date
)c )c on a.cust_no=c.cust_no
left join
(
 select distinct cust_no, 1.5 rds
from dccust.tb_fact_cust_ofs_trd_detail
where occur_date between  ? and  ?
and trim(fund_code)<>'863001'
and trim(business_code) in ('124','142')
and etl_dt between ?  and ?
--and cust_no='00001896'
)d on a.cust_no=d.cust_no
left join
(
select uid,stock_position,
    case when stock_position>=70 then 0.1
        when stock_position<70 and stock_position>=40 then 0.4
        when stock_position<40 and stock_position>0 then 0.8
        else 0.1
        end ps
    from temp.tmp_jjtj_t_user_stock_position
    where init_date = ?
 --   and cust_no='00001896'
)e1 on a.cust_no=e1.uid
left join
(
select cust_no,evaluate_value,
    case when evaluate_value='C5' then 0.5
        when evaluate_value='C4' then 0.3
        when evaluate_value='C3' then 0.2
        else 0
        end risks
from dccust.tb_fact_cust_label_risk_prop
where etl_dt= ?
)f on a.cust_no=f.cust_no
left join
(
select cust_no,
	   asset_jz+net_asset_rz  net_asset,  --净资产
	   case when asset_jz+net_asset_rz>=1000000 then 0.9
	   		when asset_jz+net_asset_rz>=500000 then 0.6
	   		when asset_jz+net_asset_rz>=200000 then 0.4
	   		when asset_jz+net_asset_rz>=50000 then 0.2
	   		when asset_jz+net_asset_rz>=10000 then 0.1
	   		when asset_jz+net_asset_rz>0 then 0
	   		when asset_jz+net_asset_rz=0 then 0.1
	   		end assets
from dccust.tb_fact_cust_attr
where occur_date = date ?
and etl_dt=?
)g on a.cust_no=g.cust_no
left join
(
select cust_no,week_id,sum(occur_bal) bnk_in,
	   case when sum(occur_bal)>=500000 then 1.5
	   		when sum(occur_bal)>=200000 then 1.2
	   		when sum(occur_bal)>0 then 0.6
	   		when sum(occur_bal)=0 then 0.1
	   		end bnks
from(
select trim(cust_no) cust_no,request_date, occur_bal,etl_dt,
weekofyear(from_unixtime(unix_timestamp(cast(request_date as string),'yyyymmdd'),'yyyy-mm-dd')) week_id
--to_char(to_date(request_date,'yyyymmdd'),'iw') week_id
from dcods.jzjyh_his_bnk_trf_ntfc
where request_type='01'
and trim(return_info)='交易成功'
and request_date between  ? and ?
and etl_dt between ? and ?
)h1
group by cust_no,week_id
)h on a.cust_no=h.cust_no
left join
(
select uid,end_time,profit_ratio,
	    case when profit_ratio>=40 then 0.2
	    when profit_ratio>=20 then 0.3
	    when profit_ratio>=-10 then 0.4
	    else 0.1
	    end rs
from temp.tmp_jjtj_t_user_profitability
where init_date= ?
)i on a.cust_no=i.uid
left join
(
select uid,max_retracement,
    case
        when max_retracement>=20 then 0.1
        when max_retracement<20  and max_retracement>=0 then 0.2
        end fs
    from temp.tmp_jjtj_t_user_max_retracement
    where init_date = ?
)j on a.cust_no=j.uid
left join
(
select uid,holdday_avg,
    case when holdday_avg>=180 then 0.1
        when holdday_avg>=90  then 0.3
        when holdday_avg>=60  then 0.4
        else 0.3
        end hs
    from temp.tmp_jjtj_t_user_hold_stock_cycle
    where init_date = ?
)k on a.cust_no=k.uid