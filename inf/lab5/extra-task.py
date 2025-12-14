import polars as pl

pl.Config.set_tbl_cols(6)
pl.Config.set_tbl_rows(15)
pl.Config.set_tbl_hide_column_data_types(True)
pl.Config.set_tbl_hide_dataframe_shape(True)
pl.Config.warn_unstable(True)


df = pl.read_excel("lab5.xlsx", has_header=False, engine="openpyxl")
df = df[3:15, 0:25]
new_df = df.drop(["column_4", "column_6"])

bin_data = []
for i in range(12):
    binary_num = ''
    for j in range(4, 23):
        binary_num += new_df[i, j]

    bin_data.append(binary_num)

new_df = new_df.drop([f"column_{i}" for i in range(7, 26)])
new_df = new_df.rename({"column_5": "column_4"})

new_col = pl.Series("column_5", bin_data)

new_df = new_df.with_columns(new_col)

print(new_df)
