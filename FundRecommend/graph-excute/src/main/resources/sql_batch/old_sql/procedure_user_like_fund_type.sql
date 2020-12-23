insert overwrite table temp.tmp_jjtj_t_user_like_fundtype partition(init_date=?)

select uid,type,cust_fundtype,null
from (
select uid,type,b.fund_type cust_fundtype from (
select cust_no uid,tag_value,'hold' as type
from (
select cust_no,fund_code
  from  dccust.tb_fact_cust_ofs_stock
  where trim(fund_code)<>'863001'
  and stock_balance>10
  and etl_dt=?
  )a
  left join
  (
select code,tag_type,tag_value
  from temp.tmp_jjtj_t_fund_stock_tags
  where init_date=?
  and species_type='fund'
  and tag_type='dfType'
  )b on trim(a.fund_code)=b.code
  group by cust_no,tag_value
  ) a
 left join
  (
  select fund_type,fund_type_name
  from temp.tmp_jjtj_t_fund_custom_type_tag
where  init_date=?
  ) b on a.tag_value=b.fund_type_name
  where b.fund_type_name is not null

  union all
  select a.uid,'suitable' as type,
		case
		-- （1）重仓、跑输大盘
-- >=70	普通股票型、增强指数  100
-- 【65，70）	被动指数型 108
-- 【50，65）	偏股混合型 101
-- 【40，50）	平衡混合型 102
-- 【30，40）	债券型 104
-- 【25，30）	绝对收益型 105
-- 【0，25）	中短期理财债券 106

		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=70 then '100'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=65 and score<70 then '108'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=50 and score<65 then '101'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=40 and score<50 then '102'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=30 and score<40 then '104'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score>=25 and score<30 then '105'
		when stock_position>=50 and b.profit_ratio<c.chg_1y and score<25 then '106'
	--	（2）重仓、跑赢大盘
-- 风险承受能力	基金类型
-- >=70	普通股票型、增强指数 100
-- 【60，70）	偏股混合型  101
-- 【50，60）	平衡混合型 102
-- 【30，50）	债券型 104
-- 【20，30）	绝对收益型 105
-- 【0，20）	中短期理财债券 106
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score>=70 then '100'
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score>=60 and score<70 then '101'
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score>=50 and score<60 then '102'
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score>=30 and score<50 then '104'
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score>=20 and score<30 then '105'
		when stock_position>=50 and b.profit_ratio>=c.chg_1y and score<20 then '106'
		-- （3）轻仓、跑赢大盘
--  风险承受能力	基金类型
-- >=60	普通股票型、增强指数 100
-- 【50，60）	偏股混合型 101
-- 【40，50）	平衡混合型 102
-- 【30，40）	债券型 104
-- 【25，30）	绝对收益型 105
-- 【0，25）	中短期理财债券 106
	when stock_position<50 and stock_position>0 and b.profit_ratio>=c.chg_1y  and d.score  >= 60.0 then '100'
	when stock_position<50 and stock_position>0  and b.profit_ratio>=c.chg_1y and d.score  >= 50.0 and d.score  < 60.0 then '101'
	when stock_position<50 and stock_position>0  and b.profit_ratio>=c.chg_1y and d.score  >= 40.0 and d.score  < 50.0 then '102'
	when stock_position<50 and stock_position>0  and b.profit_ratio>=c.chg_1y and d.score  >= 30.0 and d.score  < 40.0 then '104'
	when stock_position<50 and stock_position>0  and b.profit_ratio>=c.chg_1y and d.score  >= 25.0 and d.score  < 30.0 then '105'
	when stock_position<50 and stock_position>0  and b.profit_ratio>=c.chg_1y and d.score  < 25.0 then '106'
		-- （4）轻仓、跑输大盘
--  风险承受能力	基金类型
-- >=70	普通股票型、增强指数 100
-- 【60，70）	被动指数型 108
-- 【50，60）	偏股混合型 101
-- 【40，50）	平衡混合型 102
-- 【20，40）	债券型 104
-- 【10，20）	绝对收益型 105
-- 【0，10）	中短期理财债券 106
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 70.0 then '100'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 60.0 and d.score  < 70.0 then '108'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 50.0 and d.score  < 60.0 then '101'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 40.0 and d.score  < 50.0 then '102'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 20.0 and d.score  < 40.0 then '104'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  >= 10.0 and d.score  < 20.0 then '105'
	when stock_position<50 and stock_position>0  and b.profit_ratio<c.chg_1y and d.score  < 10.0 then '106'
		--  (5)空仓
--  风险承受能力	基金类型
-- >=60	普通股票型、增强指数 100
-- 【50，60）	偏股混合型 101
-- 【45，50）	平衡混合型 102
-- 【30，45）	债券型 104
-- 【25，30）	绝对收益型 105
-- 【0，25）	中短期理财债券 106
	when stock_position=0  and d.score >= 60.0 then '100'
	when stock_position=0  and d.score >= 50.0 and d.score < 60.0 then '101'
	when stock_position=0  and d.score >= 45.0 and d.score < 50.0 then '102'
	when stock_position=0  and d.score >= 30.0 and d.score < 45.0 then '104'
	when stock_position=0  and d.score >= 25.0 and d.score < 30.0 then '105'
	when stock_position=0  and d.score < 25.0 then '106'
	end cust_fundtype

 from (
 select uid,end_time,stock_position
 from temp.tmp_jjtj_t_user_stock_position
 where init_date= ?
  )a
  left join
  (
  select uid,end_time,profit_ratio
  from temp.tmp_jjtj_t_user_profitability
  where init_date=?
  )b on a.uid=b.uid
  left join
  (
  select secucode,tradedate,chg_1y
  from temp.tmp_jjtj_t_index_ind
  where init_date=?
  )c on b.end_time=c.tradedate
   join
  (
  select uid,score,risk_score
  from temp.tmp_jjtj_t_user_risk_tolerance
   where init_date=?
   and risk_score is not null
  )d on a.uid=d.uid
)t
group by uid,type,cust_fundtype

