insert overwrite table temp.tmp_jjtj_t_user_risk_tolerance partition(init_date=?)
select uid,b.risk_score,
    round(a.rank1/a.total*100,2) max_retracement_score,
    round(a.rank1/a.total*100*0.5+b.risk_score*0.5,2) score
from (
select uid,max_retracement,
    rank()over(order by max_retracement) rank1,
    count(*) over() total
from temp.tmp_jjtj_t_user_max_retracement t
where init_date=?
and t.uid in(
select uid from temp.tmp_jjtj_t_user_type
where init_date= ?
and (is_stock_user=1 or is_fund_user=1)
)
)a
left join
(
select cust_no,risk_tolerance,
   case when risk_tolerance='C5' then 100
        when risk_tolerance='C4' then 75
        when risk_tolerance='C3' then 50
        when risk_tolerance='C2' then 25
        when risk_tolerance='C1' then 0
        else 50
        end risk_score
from sjzxebs.tb_fact_cust_info
where cust_status='0'
and is_org='0'
and max_net_asset>0
)b on a.uid=b.cust_no
