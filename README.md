# java
建立索引库 lucene
Directory[TextField,StringField](Directory,FSDirctory)--->Analyzer--->IndexWriter(InderxConfig)--->Direcory[索引库]
建立词库，辅助查找 anjs
进行相关搜索：用户搜索--->创建查询--->执行查询， 从索引库搜索关键词--->渲染结果
