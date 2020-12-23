insert overwrite table temp.tmp_jjtj_t_user_profile_result partition(init_date = ?)

select
all_t.uid
,max(all_t.tags_preference_vector) as tags_preference_vector
,sum(all_t.buy_possibility) as buy_possibility
,sum(all_t.hold_stock_cycle) as hold_stock_cycle
,max(all_t.buy_fund_type_vector) as buy_fund_type_vector
,max(all_t.user_like_fund_type_vector) as user_like_fund_type_vector
,sum(all_t.is_push) as is_push
from (


select a.uid as uid
,concat_ws(',',collect_set(concat(b.position,'#',cast(a.score as decimal(10,2) )))) as tags_preference_vector
,0.0 as buy_possibility
,0.0 as hold_stock_cycle
,null as buy_fund_type_vector
,null as user_like_fund_type_vector
,0 as is_push
from (
 select * from temp.tmp_jjtj_t_user_tag_preference
 where init_date = ?
) a left join (
 select * from temp.tmp_jjtj_t_fund_tag_matrix_base  where init_date = ?
) b on a.tag_type = b.tag
group by a.uid


union all


select a.uid as uid
,null as tags_preference_vector
,0.0 as buy_possibility
,0.0 as hold_stock_cycle
,null as buy_fund_type_vector
,concat_ws('@',collect_set(a.cust_fundtype)) as user_like_fund_type_vector
,0 as is_push
from (
    select * from temp.tmp_jjtj_t_user_like_fundtype  where init_date =  ?
) a
group by a.uid


union all


select a.uid as uid
,null as tags_preference_vector
,0.0 as buy_possibility
,a.holdday_avg / 30  as hold_stock_cycle
,null as buy_fund_type_vector
,null as user_like_fund_type_vector
,0 as is_push
from  temp.tmp_jjtj_t_user_hold_stock_cycle a where init_date = ?



union all


 select
 a.uid as uid
,null as tags_preference_vector
,a.score as buy_possibility
,0.0  as hold_stock_cycle
,null as buy_fund_type_vector
,null as user_like_fund_type_vector
,1 as is_push
 from temp.tmp_jjtj_t_user_buy_psbility a  where init_date = ?
 and score >= 2.5


union all

select
 a.uid as uid
,null as tags_preference_vector
,0.0 as buy_possibility
,0.0  as hold_stock_cycle
,concat_ws(',',collect_set(concat(b.position,'#','1') )) as buy_fund_type_vector
,null as user_like_fund_type_vector
,0 as is_push
from temp.tmp_jjtj_t_user_buy_fund_type a
left join temp.tmp_jjtj_t_fund_type_matrix_base b on a.fund_type = b.fund_type
where a.init_date = ?
group by uid


) all_t
group by all_t.uid