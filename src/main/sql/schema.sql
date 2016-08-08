-- ���ݿ��ʼ���ű�
-- �������ݿ�
create database seckill;
-- ʹ�����ݿ�
use seckill;
-- ������ɱ����
create table seckill(
seckill_id bigint not null auto_increment comment '��Ʒ���id',
name varchar(120) not null comment '��Ʒ����',
number int not null comment '�������',
create_time timestamp not null default current_timestamp comment '����ʱ��',
start_time timestamp not null comment '��ɱ����ʱ��',
end_time timestamp not null comment '��ɱ����ʱ��',
primary key(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB auto_increment=1000 default charset=utf8 comment='��ɱ����';

-- ��ʼ������
insert into seckill(name,number,start_time,end_time)
values
	('1000Ԫ��ɱiphone6',100,'2016-08-02 00:00:00','2016-08-03 00:00:00'),
	('500Ԫ��ɱipad2',200,'2016-08-02 00:00:00','2016-08-03 00:00:00'),
	('300Ԫ��ɱС��4',300,'2016-08-02 00:00:00','2016-08-03 00:00:00'),
	('200Ԫ��ɱ����note',400,'2016-08-02 00:00:00','2016-08-03 00:00:00');
	
-- ��ɱ�ɹ���ϸ��
-- �û���½��֤��ص���Ϣ
create table success_killed(
seckill_id bigint not null comment '��ɱ��Ʒid',
user_phone bigint not null comment '�û��ֻ���',
state tinyint not null default -1 comment '״̬��ʾ��-1����Ч 0���ɹ� 1���Ѹ���',
create_time timestamp not null comment '����ʱ��',
primary key(seckill_id, user_phone),
key idx_create_time(create_time)
)ENGINE=InnoDB default charset=utf8 comment='��ɱ����';
	
-- �������ݿ����̨
mysql -uroot -p3626506
	
	