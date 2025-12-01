fn main() {
    cc::Build::new()
        .file("native/ast.c")
        .file("native/parser.c")
        .file("native/hcl.tab.c")
        .file("native/lex.yy.c")
        .compile("hclparser");
}

