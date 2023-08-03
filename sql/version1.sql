create table im_user(
`id` int not null primary key,
`name` varchar(255) not null default '',
`gender` int not null default 0
);

insert into im_user(id, name, gender) values (1, '小冯', 1);
insert into im_user(id, name, gender) values (2, '小林', 0);


