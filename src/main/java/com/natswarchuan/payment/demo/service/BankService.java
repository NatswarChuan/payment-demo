package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.dto.Bank;
import com.natswarchuan.payment.demo.interfaces.services.IBankService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Lớp dịch vụ triển khai các nghiệp vụ liên quan đến thông tin ngân hàng.
 *
 * <p>Lớp này chứa một danh sách tĩnh các ngân hàng được hỗ trợ và cung cấp phương thức để truy xuất
 * chúng.
 */
@Service
public class BankService implements IBankService {

  private static final List<Bank> SUPPORTED_BANKS =
      List.of(
          new Bank("NCB", "Ngân hàng TMCP Quốc Dân", "NCB"),
          new Bank(
              "AGRIBANK", "Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam", "Agribank"),
          new Bank("SCB", "Ngân hàng TMCP Sài Gòn", "SCB"),
          new Bank("SACOMBANK", "Ngân hàng TMCP Sài Gòn Thương Tín", "Sacombank"),
          new Bank("EXIMBANK", "Ngân hàng TMCP Xuất Nhập khẩu Việt Nam", "Eximbank"),
          new Bank("MSB", "Ngân hàng TMCP Hàng Hải", "MSB"),
          new Bank("NAMABANK", "Ngân hàng TMCP Nam Á", "Nam A Bank"),
          new Bank("VNMART", "Ví điện tử VNPAY", "VNPAY"),
          new Bank("VIETINBANK", "Ngân hàng TMCP Công Thương Việt Nam", "VietinBank"),
          new Bank("VIETCOMBANK", "Ngân hàng TMCP Ngoại Thương Việt Nam", "Vietcombank"),
          new Bank("HDBANK", "Ngân hàng TMCP Phát triển Nhà TP Hồ Chí Minh", "HDBank"),
          new Bank("DONGABANK", "Ngân hàng TMCP Đông Á", "DongA Bank"),
          new Bank("TPBANK", "Ngân hàng TMCP Tiên Phong", "TPBank"),
          new Bank("OJB", "Ngân hàng TMCP Đại Dương", "OceanBank"),
          new Bank("BIDV", "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam", "BIDV"),
          new Bank("TECHCOMBANK", "Ngân hàng TMCP Kỹ thương Việt Nam", "Techcombank"),
          new Bank("VPBANK", "Ngân hàng TMCP Việt Nam Thịnh Vượng", "VPBank"),
          new Bank("MBBANK", "Ngân hàng TMCP Quân Đội", "MBBank"),
          new Bank("ACB", "Ngân hàng TMCP Á Châu", "ACB"),
          new Bank("OCB", "Ngân hàng TMCP Phương Đông", "OCB"),
          new Bank("IVB", "Ngân hàng TNHH Indovina", "IVB"),
          new Bank("VISA", "Thẻ quốc tế Visa", "Visa"),
          new Bank("MASTERCARD", "Thẻ quốc tế MasterCard", "MasterCard"),
          new Bank("JCB", "Thẻ quốc tế JCB", "JCB"));

  /** {@inheritDoc} */
  @Override
  public List<Bank> getSupportedBanks() {
    return SUPPORTED_BANKS.stream()
        .sorted(Comparator.comparing(Bank::shortName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }
}
