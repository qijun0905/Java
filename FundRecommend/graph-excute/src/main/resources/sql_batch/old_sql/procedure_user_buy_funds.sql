insert overwrite table temp.tmp_jjtj_t_user_buy_funds partition(init_date = ?)
select t.uid,t.fund_code,t.app_amt,t.cfm_amt,t.occur_date
from  (
select  cust_no as uid,fund_code,app_amt,cfm_amt,occur_date,
row_number() over(partition by cust_no,fund_code order by etl_dt desc) as rn
from dccust.tb_fact_cust_ofs_trd_detail
where app_amt > 1  and fund_code<>'863001'
and  trim(business_code) in ('120','122')
and occur_date <= date ? and etl_dt <= ?
 ) t where t.rn = 1