import BoardList from '../components/BoardList.jsx';
import axios from 'axios';
import { useEffect, useState } from 'react';

const PostlistPage = () => {
  const [boardList, setBoardList] = useState([]);
  const config = {
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS',
    },
  };
  const getBoardlist = async () => {
    try {
      const response = await axios.get(
        `http://ec2-54-180-158-15.ap-northeast-2.compute.amazonaws.com:8080/board/integrated`,
        config
      );
      console.log('status:', response.status);
      console.log('data:', response.data);
      return response.data;
    } catch (error) {
      console.error(error);
    }
  };
  useEffect(() => {
    getBoardlist().then((data) => {
      setBoardList(data);
    });
  }, []);

  return (
    <div>
      <BoardList boardList={boardList} />
    </div>
  );
};

export default PostlistPage;
