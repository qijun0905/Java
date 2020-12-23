insert overwrite table temp.tmp_jjtj_t_user_buy_fund_type partition(init_date = ?)
select a.uid,a.fund_code,c.fund_type from temp.tmp_jjtj_t_user_buy_funds a
left join (
	select * from temp.tmp_jjtj_t_fund_stock_tags
	where species_type = 'fund' and tag_type = 'dfType' and init_date =  ?
) b on a.fund_code = b.code
left join (
	select * from temp.tmp_jjtj_t_fund_custom_type_tag where init_date =  ?
) c on b.tag_value = c.fund_type_name
where a.init_date = ? and b.code  is not null and c.fund_type is not null