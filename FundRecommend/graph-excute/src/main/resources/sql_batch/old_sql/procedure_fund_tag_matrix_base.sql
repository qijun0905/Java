insert overwrite table temp.tmp_jjtj_t_fund_tag_matrix_base partition(init_date = ?)
select tag, row_number() over(order by tag) from (
select concat_ws('#',tag_value,tag_type) as tag from temp.tmp_jjtj_t_fund_stock_tags
where init_date = ?
group by concat_ws('#',tag_value,tag_type)
) t