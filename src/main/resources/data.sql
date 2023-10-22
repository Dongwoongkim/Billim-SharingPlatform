-- ROLE
INSERT INTO role (role_type)
values ('ADMIN'),
       ('MANAGER'),
       ('USER');

-- MEMBER // password : 1q2w3e4r!!
INSERT INTO member
(username, password, nickname, city, district, street, zip_code, introduce, phone_number, profile_image_id, provider)
values
    ('admin123@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'admin','서울시', '관악구', '신림동', '56-1', '안녕하세요 ADMIN입니다.', '011-3124-3678', null, 'None'),
    ('manager123@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'manager','서울시', '강북구', '미아동', '56-3', '안녕하세요 MANAGER입니다.', '011-1311-5478', null, 'None'),
    ('user123@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'user','서울시', '광진구', '화양동', '620-1', '안녕하세요 USER입니다. 잘부탁드립니다.', '011-3134-7623', null, 'None'),
    ('kdo6301@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'ehddnd', '전주시', '덕진구', '금암동', '709-4', '안녕하세요 김동웅입니다. 잘부탁드립니다.', '012-6606-3323', null, 'None'),
    ('dudwo0926@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'dudwo','전주시', '덕진구', '금암동', '709-3', '안녕하세요 박영재입니다. 잘부탁드립니다.', '011-3134-7623', null, 'None'),
    ('leesj@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'tmdwo','전주시', '덕진구', '금암동', '709-3', '안녕하세요 이승재입니다. 잘부탁드립니다.', '011-1111-7223', null, 'None'),
    ('choims@naver.com', '$2a$10$LGyuYTpUWfDwlBK.rSak3.U.0TuIFl8r5dU8H9w1EOAI0rn0fnqf2', 'audtn','전주시', '덕진구', '금암동', '702-3', '안녕하세요 최명수입니다. 잘부탁드립니다.', '011-3134-7623', null, 'None');



INSERT INTO member_role (member_id, role_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (2, 3),
       (3, 3),
       (4, 3),
       (5, 3),
       (6, 3),
       (7, 3);


-- CATEGORY
INSERT INTO category (name, parent_id)
values ('공지사항', null),
       ('대여거래 카테고리', null),
       ('가전제품', 2),
       ('테블릿', 3),
       ('웨어러블', 3),
       ('오디오/영상관련 기기', 3),
       ('기타', 3),
       ('생활용품', 2),
       ('주방용품', 8),
       ('욕실용품', 8),
       ('기타', 8),
       ('완구', 2),
       ('보드게임', 12),
       ('퍼즐', 12),
       ('레고', 12),
       ('기타', 12),
       ('운동기구', 2),
       ('낚시', 17),
       ('캠핑', 17),
       ('기타', 17),
       ('차량, 오토바이', 2),
       ('악기', 2),
       ('현악기', 22),
       ('관악기', 22),
       ('타악기', 22),
       ('국악기', 22),
       ('혼성악기', 22),
       ('책', 2),
       ('소설', 28),
       ('자기개발서', 28),
       ('역사/인문학', 28),
       ('과학', 28),
       ('시/에세이', 28),
       ('공구', 2),
       ('의류', 2),
       ('여성의류', 35),
       ('남성의류', 35),
       ('가방', 35),
       ('커뮤니티', null),
       ('자유게시판', 39),
       ('신고게시판', 39);


INSERT INTO post (created_time, last_modified, category_id, content, name, price, quantity, likes, member_id, title, complete)
values (now(), now(), 1, '공지사항 내용입니다.', null, null, null, 0, 1, '필독 공지사항', false),
       (now(), now(), 4, '아이패드5 PRO 대여해드립니다.', '아이패드 프로5', 50000, 1, 0, 3, '아이패드5 PRO 대여해드립니다.', true),
       (now(), now(), 13, '할리갈리, 마피아, 뱅 등 다수 보드게임 대여해드립니다. 자세한 사항은 쪽지나 채팅주세요', '보드 게임', 5000, 1, 0, 3, '보드게임 대여해드립니다. 종류 많음', true);


INSERT INTO comment (created_time, last_modified, content, deleted, member_id, parent_id, post_id, root)
values
    (now(), now(), '혹시 다빈치코드 있나요?', false, 4, null, 3, true),
    (now(), now(), '네 있습니다.', false, 3, 1, 3, false),
    (now(), now(), '대여하고 싶습니다..', false, 5, null, 3, true),
    (now(), now(), '쪽지 주세요!', false, 3, 3, 3, false);


INSERT INTO message (content, delete_by_receiver, delete_by_sender, receiver_id, sender_id, created_time, checked, post_id)
values ('okvv26입니다. 대여하고 싶어서 쪽지 드렸습니다.', false, false, 3, 5, now(), false, 2),
       ('kdo6301입니다. 다빈치 코드 8월1일부터 일주일간 대여하고 싶습니다. 혹시 게시글에 쓰여진 가격이 하루당 가격인가요?', false, false, 3, 4, now(), false, 3),
       ('네. 근데 일주일간 빌린다고 하시면 어느정도 깎아드릴게요!!', false, false, 4, 3, now(), false, 3);


INSERT INTO trade (render_id, start_date, end_date, trade_complete, written_review, borrower_id, post_id, review_id)
values (3, now(), now(), true, false, 4, 2, null),
       (3, now(), now(), true, false, 5, 3, null);


INSERT INTO report (content, report_type, post_id, reported_id, reporter_id)
values ('좋아요 누른 게시글 조회가 안됩니다.', 'BUG', null, null, 2),
       ('적절하지 않은 게시물 입니다...', 'POST_REPORT', 3, 3, 2);

