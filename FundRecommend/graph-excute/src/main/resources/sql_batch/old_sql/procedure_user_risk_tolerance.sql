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
where init_date=?
and (is_stock_user=1 or is_fund_user=1)
)
)a
left join
(
select cust_no,evaluate_value,
    case when substr(evaluate_value,1,2)='C1' then 0
        when evaluate_value='C2' then 25
        when evaluate_value='C3' then 50
        when evaluate_value='C4' then 75
        when evaluate_value='C5' then 100
        else 50
    end risk_score
from dccust.tb_fact_cust_label_risk_prop
where etl_dt= ?
)b on a.uid=b.cust_no