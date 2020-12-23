insert overwrite table temp.tmp_jjtj_t_fund_type_matrix_base partition(init_date = ' bt 20200911')
select fund_type , row_number() over(order by fund_type ) from (
select fund_type from temp.tmp_jjtj_t_fund_custom_type_tag
where init_date = 'bt 20200911'
group by fund_type
) t