import styled from 'styled-components';
// 프로젝트 깃허브 페이지 추가.
// 넓이 수정.
function Footer() {
  return (
    <Footerbody>
      <Footermain>
        ©Copyright ⓒ 2023·MBTI=Science·프론트:
        <Footerbox />
        <Footera href="https://github.com/yeojuyeong">여주영</Footera>,
        <Footera href="https://github.com/hyeonhyeon-K">김재현</Footera>,
        <Footera href="https://velog.io/@mikjk0530">김준경</Footera>,
        <Footera href="https://github.com/ByeongminBRO">안병민</Footera>
        <Footerbox />
        ·백엔드:
        <Footera href="https://github.com/5selny">박세련</Footera>,
        <Footera href="https://github.com/klouxia">박철우</Footera>,
        <Footera href="https://github.com/sunwjung">정순원</Footera>,
        <Footera href="https://github.com/p1Zzal">이현수</Footera>
      </Footermain>
    </Footerbody>
  );
}

const Footerbody = styled.div`
  display: flex;
  background-color: #9bcbf8;
  width: 100%;
  height: 5vh;
  position: fixed;
  bottom: 0;
  left: 0;
  text-align: left;
`;
const Footermain = styled.div`
  display: flex;
  justify-content: space-between;
  color: white;
  font-size: 1.3rem;
  margin-left: 30px;
  margin-top: 4px;
  font-weight: 500;
  bottom: 0;
`;
const Footera = styled.a`
  color: white;
  letter-spacing: 1px;
`;
const Footerbox = styled.div`
  width: 10px;
`;
export default Footer;
