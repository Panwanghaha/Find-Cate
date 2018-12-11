package team.j2e8.findcateserver.services;

import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import team.j2e8.findcateserver.infrastructure.usercontext.IdentityContext;
import team.j2e8.findcateserver.models.*;
import team.j2e8.findcateserver.repositories.*;
import team.j2e8.findcateserver.utils.HttpResponseDataUtil;
import team.j2e8.findcateserver.exceptions.ResourceNotFoundException;
import team.j2e8.findcateserver.utils.Encryption;
import team.j2e8.findcateserver.utils.EnsureDataUtil;
import team.j2e8.findcateserver.valueObjects.ErrorMessage;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;

/**
 * @auther vinsonws
 * @date 2018/12/11 21:18
 */
@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;
    @Resource(name = "tokenIdentityContext")
    private IdentityContext identityContext;

    private QShop qShop = QShop.shop;
    private QType qType = QType.type;
    private QFood qFood = QFood.food;
    private QAdmin qAdmin = QAdmin.admin;
    private QUser  qUser = QUser.user;

    public Page<Shop> getAllShopByUser( String sort, int pageNum, int pageSize){
        User user = (User) identityContext.getIdentity();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qShop.user.id.eq(user.getId()));
        return shopRepository.findAll(booleanBuilder, HttpResponseDataUtil.sortAndPaging(sort, pageNum, pageSize));
    }

    public Page<Shop> getShopById(Integer shopId, String sort, int pageNum, int pageSize){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qShop.shopId.eq(shopId));
        return shopRepository.findAll(booleanBuilder, HttpResponseDataUtil.sortAndPaging(sort, pageNum, pageSize));
    }
    //将token转为完整的user对象
    private User TokenToUser(){
        User user = (User) identityContext.getIdentity();
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(qUser.id.eq(user.getId()));
        Optional<User> user1 = userRepository.findOne(booleanBuilder);
        return user1.get();
    }
    private Boolean judgeAdmin(User user){
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(qAdmin.adminId.eq(user.getId()));
        //查询
        Optional<Admin> optionalAdmin = adminRepository.findOne(booleanBuilder);
        return optionalAdmin.isPresent();
    }

    //判断密码是否正确
    private Boolean judgePassword(String password)throws Exception{
        User user = TokenToUser();
        Encryption encryption = new Encryption();
        String pass = encryption.getPassword(password.trim(), user.getUserSalt());
        return user.getUserPassword().equals(pass);
    }

    public void registerShop(String password,String shopName,String shopAddr,String shopTelenumber,String shopPhoto) throws Exception{
        EnsureDataUtil.ensureNotEmptyData(password, ErrorMessage.EMPTY_PASSWORD.getMessage());
        if(!judgePassword(password)){
            throw new ResourceNotFoundException(ErrorMessage.ERROR_LOGIN__NAME_OR_PASSWORD);
        }

        Shop shop = new Shop();
        shop.setShopTelenumber(shopTelenumber);
        shop.setShopName(shopName);
        shop.setShopAddress(shopAddr);
        shop.setShopPhoto(shopPhoto);
        shop.setUser(TokenToUser());
        shop.setShopActive(0);
        shopRepository.save(shop);
    }


    public Iterable<Shop> getDisactiveShops(){
        User user = TokenToUser();
        if(!judgeAdmin(user)){
            throw new ResourceNotFoundException(ErrorMessage.ACCOUNT_NOT_EXIST);
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(qShop.shopActive.eq(0));
        Iterable<Shop> shops = shopRepository.findAll(booleanBuilder);
//        List<Shop> lShops = new ArrayList<>();
//        shops.forEach(single->{lShops.add(single);});
        return shops;
    }

    public void activeShop(int id){
        User user = TokenToUser();
        if(!judgeAdmin(user)){
            throw new ResourceNotFoundException(ErrorMessage.ACCOUNT_NOT_EXIST);
        }
        Optional<Shop> shops = shopRepository.findById(id);
        Shop shop = shops.get();
        shop.setShopActive(1);
        shopRepository.save(shop);
    }
}