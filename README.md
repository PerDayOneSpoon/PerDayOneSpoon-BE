## ✨ 프로젝트 소개

### 하루에 한 줌씩 🧑🏻‍🌾🌱 꾸준히 습관을 기록해보자! <br><br>

> 하루 한 줌은 체득하고 싶은 습관들을 기록하여 꾸준히 이뤄나갈 수 있도록 도와주는 서비스입니다.<br><br>
> 사용자들이 단순히 자신의 습관을 만드는 것보다 좀 더 재미있게 습관을 형성할 수 있도록 하자는 취지에서 시작된 프로젝트입니다. <br>
> 따라서 내 습관뿐만 아니라 친구의 습관까지 확인하고 독려할 수 있습니다.
<br>

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmtAhG%2FbtrNhOL2bvc%2FnjDlPCDAfNPXkU5q19MNq0%2Fimg.jpg" width="800">

- **[하루한줌 바로가기](https://www.perday-onespoon.com/)<br>**
- **[발표 자료](https://docs.google.com/presentation/d/1u2x1SL4Bt863htJeiWeb8mTztDs20Rne1hU_DN310EU/edit?usp=sharing)<br>**
- **[팀 노션 주소](https://www.notion.so/3-8b744f1d04da4c41812b94df4ad65035)**
- **[시연 영상](https://youtu.be/PDkd_5A_j4k)<br>**
  <br>
  <br>

## 📆 프로젝트 기간 <br>

<ul>
  <li>개발 기간: 2022/08/26 ~ 2022/10/07</li>
  <li>런칭: 2022/10/03</li>
  <li>유저 피드백: 2022/10/03 ~ 2022/10/07</li>
  <li>추가 업데이트: 2022/10/03 ~ 진행 중</li>
</ul>


<br>
<br>

## 📖 서비스 아키텍쳐<br>

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbg3Vqy%2FbtrNjyBAtmG%2Fz58lk6MglF7kHzwkWhkgBK%2Fimg.png)

<br>
<br>

## 👊 아키텍쳐 도입 배경<br>
<details> 
  <summary><strong>Git Action</strong></summary><br>
  <li> CI & CD 구축 당시 구축된 환경에서 팀원들이 개발에만 집중할 수 있게 만드려는 것이 우리의 중점 과제였다.</li>
  <li> 대안으로는 Genkins Travis가 존재했으나 둘다 EC2서버를 두대로 CI & CD 구축해야 한다는 차이점이 존재했다.</li>
  <li> Git Action은 하나의 서버로 CI & CD구축이 가능하여 서버 비용의 문제 감당 시 비용 최소화를 할 수 있다고 생각했다.</li>
  <li> 레퍼런스도 많고 러닝커브가 적으며 원격 저장소로 Git Hub를 사용하는 우리에겐 git action은 난이도도 적용하기도 제일 쉽다고 생각했다.</li>
  <li> 상기 이유들로 비용 최소화 , 최소한의 시간으로 구축된 환경을 만족한다고 생각하여 Git Action으로 자동 배포환경을 구축했다.</li>
</details>
<details> 
  <summary><strong>google, kakao, naver 소셜로그인 </strong></summary><br>
  <li> 로그인을 구현하게 되었을 때 사용자들의 편의성을 고려하는 단계에서 일반 로그인은 편의성을 떨어뜨린다고 판단했다.</li>
  <li> 소셜 로그인으로 인증 , 인가를 보증된 소셜(kakao등)에 맡겨 간편한 로그인 처리 방식으로 편의성을 향상시키고자 하였다.</li>
  <li> 소셜 로그인 중 애플의 경우 (1년간 9~12만원의 비용) 결제금액의 이슈로 카카오 , 네이버 , 구글 3개의 소셜로그인을 선택하게 되었다.</li>
  <li> git hub는 일반 사용자들에겐 접근성이 떨어진다고 판단했고 facebook은 naver, goolge로 대체 가능하다 판단했다.</li>
  <li> 상기 이유들로 3개의 소셜 로그인을 선택하게 되었고 그에따라 편의성을 향상시킬 수 있었다.</li>
</details>
<details> 
  <summary><strong>Redis</strong></summary><br>
  <li> 데이터의 I/O가 잦은 경우 변동성이 적은 데이터일때 매번 DB를 조회하는 것은 트래픽 부하와 성능 저하를 해결할 수 없었다.</li>
  <li> 데이터를 캐싱 처리하는 경우 트래픽을 줄이고 성능을 향상시킬 수 있는데 이때 로컬캐시 , Redis를 고려하게 되었다.</li>
  <li> 로컬캐시(caffeine cache)를 고려하게 되었으나 무중단 배포 환경에서 휘발성 캐시가 사라질 위험이 존재한다고 판단했고 scale-out시 데이터 정합성 문제가 생긴다고 판단했다.</li>
  <li> Redis의 경우 여러 자료구를 지원하여 캐싱 처리, 데이터를 처리하기 편리하다고 생각했고 무중단 배포환경에서 서버의 자원을 사용하기에 데이터가 사라질 위험이 존재하지 않았다.</li>
  <li> Redis는 여러 서버간 데이터 정합성 문제도 해결할 수 있다고 생각했다.</li>
  <li> 상기 이유들로 Redis를 캐싱처리를 위해 사용하기로 결정했다.</li>
</details>
<details> 
  <summary><strong>aws RDS MySql</strong></summary><br>
  <li> DB를 저장하기 위한 RDBMS로는 RDBS와 NOSQL이 존재한다.</li>
  <li> NOSQL은 검색속도가 월등하나 테이블간 연관관계를 설정할 수 없고 데이터의 형태가 정확하게 유지되지 않으며 데이터의 무결성이 <br>지켜지지 않는다.</li>
  <li> RDBMS는 데이터의 무결성이 지켜지며 일정한 스키마로 데이터를 관리할 수 있어 테이블 내 데이터를 각각 관리할 스트레스가 줄어들며 <br>연관관계로 테이블들을 관리할 수 있다.</li>
  <li> 상기 이유들로 RDBMS를 선택했으며 aws의 RDS인 MySql을 사용하기로 결정했다.</li>
</details>
<details> 
  <summary><strong>router53 , Amazon ELB</strong></summary><br>
  <li> Front-End와 통신시 HTTP프로토콜로만 통신하는 것은 보안상의 위험성을 야기한다고 생각한다.</li>
  <li> Back-End 배포시 HTTPS 프로토콜을 사용하여 보안을 높히고자 하였고 이때 aws의 router53, Amazon ELB를 도입하는 것이 EC2를 사용하는 우리가 바로 적용할 수 있는 부분이라고 생각했다.</li>
  <li> 상기 이유들로 HTTP,HTTPS프로토콜을 통신할 수 있는 배포 환경을 구축하는 것에 aws의 router53과 Amazon ELB를 이용하기로 결정했다. </li>
</details>
<details> 
  <summary><strong>SSE</strong></summary><br>
  <li> 실시간 알림을 구현하기 위해선 기존의 HTTP 통신 방식(폴링 , 긴폴링)을 사용하기엔 자원의 낭비가 발생하여 새로운 방식을 도입해야 했다.</li>
  <li> 기존의 HTTP프로토콜을 사용하는 streaming방식의 SSE와 WebSocket을 사용하는 웹소켓 두가지가 존재했으나 우리가 구현하려는 알림은 <br>양방향의 알림이 아니었다.</li>
  <li> 배터리 소모량이 적고 연결이 끊어지면 재연결을 시도하며 pollyfill로 모든 브라우저 지원이 가능하게할 수 있는 SSE가 우리의 알림과 <br>맞는다고 판단했다.</li>
  <li> SSE는 첫 연결 이후 매번 재요청을 하지않고 서버의 응답을 줄 수 있어 비용을 아낄 수 있는 측면과 웹소켓의 차이 , 프로젝트의 방향성을 <br>고려하여 사용하기로 결정했다.</li>
</details>
<br>
<br>

## 💖 주요 기능

<details>

  <summary><strong>📅지키고 싶은 습관들을 기록해 캘린더에서 확인할 수 있어요.</strong></summary>

  <br/>

  <ul>
    <li>시간과 캐릭터를 선택할 수 있습니다.</li>
    <li>습관은 3일과 7일 중에 선택할 수 있습니다.</li>
    <li>설정한 시간으로 타이머를 진행하고 습관을 달성할 수 있습니다.</li>
    <li>설정한 습관을 캘린더에서도 확인할 수 있습니다.</li>
    
<br>

  <img src="https://user-images.githubusercontent.com/84265783/194710362-81fb0bb3-8dfe-420e-996b-9fe94b28b3da.gif" width="300">
  <img src="https://user-images.githubusercontent.com/84265783/194705245-90084918-5f83-495f-8804-9bbcd0e6fa8a.gif" width="300">
    <img src="https://user-images.githubusercontent.com/84265783/194713505-59592dd2-dcd3-4d8b-b85f-9462b5940d3d.gif" width="300">
<br>

  </ul>

</details>

<details>

  <summary><strong> 🙌친구를 검색하여 팔로우하고 선택하면 친구가 기록한 습관을 확인할 수 있어요.</strong></summary>

  <br/>

  <ul>

<li>친구의 이메일, 이름 또는 검색코드를 사용하여 검색할 수 있습니다.</li>
<li>캘린더에서 친구가 공개 설정한 습관을 확인할 수 있습니다.</li>
    <br/>

<img src="https://user-images.githubusercontent.com/84265783/194705193-8292ef03-5278-49c2-8176-1591f9f20470.gif" width="300">
<img src="https://user-images.githubusercontent.com/84265783/194705267-d68aebd3-08f6-4757-a9f3-4abd11dfe066.gif" width="300">
    

  </ul>

</details>

<details>

  <summary><strong> 👀친구의 습관에 좋아요와 댓글을 달아 소통할 수 있어요!</strong></summary>

  <br/>

  <ul>

  <li>친구가 어떤 습관을 했는지 둘러보고 응원과 코멘트를 남길 수 있습니다.</li>
    <br/>

<img src="https://user-images.githubusercontent.com/84265783/194709252-57c689ea-0399-4a0f-a639-9fa06cd8e618.gif" width="300">

  </ul>

</details>

<details>

  <summary><strong>👫친구에게 보여주고 싶지 않은 습관들은 프라이빗 설정으로 숨길 수 있어요!</strong></summary>

<br/>

<ul>
  <li>프라이빗을 설정한 습관은 친구들이 캘린더에서 볼 수 없습니다.</li>
  <br />
  
<img src="https://user-images.githubusercontent.com/84265783/194713335-245c547b-7203-4546-97d2-92d842239e8d.gif" width="300">

</ul>

</details>

<details>

  <summary><strong>🏅뱃지들은 어떻게 얻는지 비밀..! 서비스를 이용하면서 하나씩 얻어가는 재미를 느껴보세요!</strong></summary>

  <br/>

  <ul>

<li>얻은 뱃지들은 이미지와 함께 언제 획득했는지 알 수 있습니다.</li>
<li>얻지 않은 뱃지들은 물음표 모양의 뱃지와 함께 힌트를 제공합니다.</li>
    <br/>    

<img src="https://user-images.githubusercontent.com/84265783/194710480-997ca3db-83e9-4712-a59a-e0154a1dd91e.gif" width="300">
    <br>

  </ul>

</details>

<details>

  <summary><strong>📢실시간으로 알림을 받을 수 있어요!</strong></summary>

  <br/>

  <ul>

  <li>뱃지 획득, 댓글, 좋아요, 습관 달성 시 실시간으로 알림을 받을 수 있습니다.</li>
    <br/>

<img src="https://user-images.githubusercontent.com/84265783/194705255-4b4a48fc-f99b-4e1d-ab42-25aed71f6070.gif" width="300">
<img src="https://user-images.githubusercontent.com/84265783/194705258-24af8f5a-8ffc-463c-8f7d-0f8546e2d238.gif" width="300">

  </ul>

</details>

<details>

  <summary><strong>🙍‍♂️내 프로필을 편집할 수 있어요!</strong></summary>

  <br/>

  <ul>

  <li>사진과 이름, 상태메시지를 변경할 수 있습니다.</li>
  <li>친구들의 상태메시지는 팔로우 또는 팔로워 목록에서 확인할 수 있습니다.</li>
    <br/>

<img src="https://user-images.githubusercontent.com/84265783/194712944-8d4b2dc3-5897-46f2-ac3d-c590dda5df3c.gif" width="300">
    <img src="https://user-images.githubusercontent.com/84265783/194710374-d72647b4-ac21-45ff-b107-3d252be5239d.gif" width="300">

  </ul>

</details>

<br>
<br>

## 👪 TEAM 소개

|                                                           [김민섭](https://github.com/alstjq8251) 리더                                                           |                                                                              [박민혁](https://github.com/Park-Seaweed)                                                                              |                                                      [최명순](https://github.com/roy656)                                                      |                                              [전소연](https://github.com/soyeon102) 부리더                                              |                                                                               [배지영](https://github.com/BaejiGongju)                                                                               |
| :--------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|                                <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                |                                                 <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                                  |                      <img src="https://img.shields.io/badge/Back end-fcfd82?style=for-the-badge&logo=&logoColor=white">                       |                   <img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white">                   |                                                 <img src="https://img.shields.io/badge/front end-fcfd82?style=for-the-badge&logo=&logoColor=white">                                                  |
| ![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDO9Ma%2FbtrNhOrVyfo%2F0tAlwnBSxOvKYDMD682Zik%2Fimg.png) | ![KakaoTalk_Photo_2022-03-30-14-34-07](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FzR6lR%2FbtrNjzHoynR%2FI4iKHEHRzPhXzKSm8xWxL0%2Fimg.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://user-images.githubusercontent.com/79740505/161509182-6a56457f-b0e6-45f0-b40e-d95cbf48619c.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/KakaoTalk_Photo_2022-09-29-22-08-14.png) | ![KakaoTalk_Photo_2022-03-30-14-41-33](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcb1y70%2FbtrNjz1HUuc%2FeMbRbc12c8KQWzWLGTWKsK%2Fimg.png) |

<br>

## 🔧 기술 스택

### 💻 백엔드

<br>
<br>

 <p align="center">
 <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
 <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
 <img src="https://img.shields.io/badge/-Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
 <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
 <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon CloudWatch-FF4F8B?style=for-the-badge&logo=Amazon CloudWatch&logoColor=white">
 <img src="https://img.shields.io/badge/Apache JMeter-D22128?style=for-the-badge&logo=Apache JMeter&logoColor=white">
 <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
 <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
 <img src="https://img.shields.io/badge/QueryDsl-2088FF?style=for-the-badge&logo=&logoColor=white"> 
 <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
 <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">
 <img src="https://img.shields.io/badge/SSL-721412?style=for-the-badge&logo=SSL&logoColor=white">
 </p>

 <br>
 <br>

### 💻 프론트엔드

<br>

<p align="center">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
  <img src="https://img.shields.io/badge/React Query-FF4154?style=for-the-badge&logo=React Query&logoColor=white">
   <img src="https://img.shields.io/badge/Recoil-2088FF?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white">
  <img src="https://img.shields.io/badge/styled components-DB7093?style=for-the-badge&logo=styled components&logoColor=white">
  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white">

  <br>
  <br>


## 🎇 개발 포인트

- **[ERD](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/ERD)<br>**
- [무중단 배포]()
- **[이미지 리사이징](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%A6%AC%EC%82%AC%EC%9D%B4%EC%A7%95)<br>**
- **[Jasypt 암호화,복호화 적용](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Jasypt-%EC%95%94%ED%98%B8%ED%99%94-,-%EB%B3%B5%ED%98%B8%ED%99%94-application-yaml%ED%8C%8C%EC%9D%BC-%EC%A0%81%EC%9A%A9)<br>**
- **[SSE 적용 간 서비스 강결합 문제 해결](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/SSE-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%B0%9C%ED%96%89-,-%EA%B5%AC%EB%8F%85-%EC%A0%81%EC%9A%A9-%EB%B0%8F-%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4-%EC%88%9C%EC%84%9C%EC%97%90-%EB%94%B0%EB%A5%B8-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EB%B0%9C%EC%83%9D-%EC%A0%81%EC%9A%A9(@TransactionalEventListner-,-@Transactional))<br>**
- **[swagger 적용](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/swagger-%EC%A0%81%EC%9A%A9)<br>**
- **[QueryDsl 동적 쿼리 적용](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/QueryDsl-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC)<br>**
- **[QueryDsl Jpa 성능 최적화 적용](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/JPA-N%E2%9E%95-1-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94)<br>**
- **[mapstruct 도입](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Map-Struct-%EB%8F%84%EC%9E%85)<br>**

<br>
<br>

## 🚀 트러블슈팅

- **[소셜로그인-코드변조](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%A0%81%EC%9A%A9(%EC%9D%B8%EA%B0%80-%EC%BD%94%EB%93%9C-%EB%B3%80%EC%A1%B0))<br>**
- **[linux - 이미지 업로드](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Linux-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%8B%A4%ED%8C%A8-%ED%98%84%EC%83%81)<br>**
- **[QueryDsl - N+1 , 카테시안 곱 , multiplebag](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/QueryDsl-Fetchjoin(%EC%B9%B4%ED%85%8C%EC%8B%9C%EC%95%88-%EA%B3%B1(Cartesian-Product),-Multiplebag-%EB%AC%B8%EC%A0%9C))<br>**
- **[hikari pool time out](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/Hikari-pool-time-out)<br>**
- **[ec2 메모리-스왑](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/EC2-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%82%AC%EC%9A%A9%EB%9F%89%EC%9C%BC%EB%A1%9C-%EC%9D%B8%ED%95%B4-%EC%84%9C%EB%B2%84-%EB%8B%A4%EC%9A%B4)<br>**
- **[SSE 적용 간 리눅스 환경 설정 문제](https://github.com/PerDayOneSpoon/PerDayOneSpoon-BE/wiki/SSE-%EC%A0%81%EC%9A%A9-(%EB%A6%AC%EB%B2%84%EC%8A%A4-%ED%94%84%EB%A1%9D%EC%8B%9C-%ED%99%98%EA%B2%BD-%EC%84%A4%EC%A0%95-))<br>**
