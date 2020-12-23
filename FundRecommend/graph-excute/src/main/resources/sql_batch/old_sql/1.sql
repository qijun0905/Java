insert overwrite table temp.tmp_jjtj_t_user_tag_preference partition(init_date = '20200904')
select t.uid
,concat_ws('#',t.tag_value,t.tag_type) as tag_type
,t.tag_value,nvl(sum(t.score * t.weight),0.0) as score
from (
select a.uid,a.score,a.fund_code,c.tag_type,c.tag_value,c.weight from
temp.tmp_jjtj_t_user_hold_fund_preference a
left join (
select * from temp.tmp_jjtj_t_fund_stock_tags
where init_date = '20200910'
and species_type = 'fund'
) c on trim(a.fund_code) = trim(c.code)
where c.code is not null and a.init_date = '20200904'


union all

select a.uid,a.score,a.stock_code as fund_code,c.tag_type,c.tag_value,c.weight
from temp.tmp_jjtj_t_user_hold_stock_preference a
left join (
select * from temp.tmp_jjtj_t_fund_stock_tags
where init_date = '20200910'
and species_type = 'stock'
) c on trim(a.stock_code) = trim(c.code)
where c.code is not null  and a.init_date = '20200904'

) t group by t.uid,t.tag_type,t.tag_value
;