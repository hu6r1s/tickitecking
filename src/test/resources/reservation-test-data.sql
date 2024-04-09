insert into auditoriums (created_at, deleted_at, modified_at, address, company_user_id, max_column, max_row, name)
values (NOW(), null, NOW(), '강남구', 1, '10', 'D', '좋은 공연장');

insert into concerts (created_at, deleted_at, modified_at, auditorium_id, company_user_id, description, name, start_time)
values (now(), null, now(), 1, 1, '아이유의 콘서트', '아이유 콘서트', now());

insert into seats (auditorium_id, availability, concert_id, grade, horizontal, reserved, vertical)
values (1, 'Y', 1, 'G', 'A', 'N', '0');

