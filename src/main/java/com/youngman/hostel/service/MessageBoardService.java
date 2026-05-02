package com.youngman.hostel.service;
import com.youngman.hostel.entity.MessageBoard;
import java.util.List;
public interface MessageBoardService {
    List<MessageBoard> listLatestPublished(int limit);
    List<MessageBoard> listPublished();
    Long save(MessageBoard message);
    boolean audit(Long id, Integer status);
    boolean delete(Long id);
}
