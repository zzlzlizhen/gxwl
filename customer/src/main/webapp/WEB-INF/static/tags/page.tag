<%@tag pageEncoding="utf-8" description="分页" %>
<%@attribute name="page" type="com.remote.pageutil.Page" required="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
    <tr>
        <td>
            <span class="page-info">{共有记录${page.totalCount}条/共}</span>
        </td>
    </tr>
    <tr>
        <td>
            <c:choose>
                <c:when test="${page.nowPage==1}">
                    首页&nbsp;&nbsp;
                    上一页&nbsp;&nbsp;
                </c:when>
                <c:otherwise>
                    <a href="#" onclick="turnPage(1);" title="首页">首页</a>
                    <a href="#" onclick="turnPage(${page.nowPage - 1});" title="上一页">上一页</a>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${current eq page.nowPage}">
                    <font color="red">${page.nowPage }</font>
                    </c:when>
                    <c:otherwise>
                        <a href="#" onclick="turnPage(${page.nowPage })">${page.nowPage }</a>
                    </c:otherwise>
              </c:choose>
              <c:choose>
                    <c:when test="${page.nowPage==1}">
                        首页&nbsp;&nbsp;
                        上一页&nbsp;&nbsp;
                    </c:when>
                    <c:otherwise>
                        <a href="#" onclick="turnPage(${page.nowPage }+ 1);" title="下一页">下一页</a>
                        <a href="#" onclick="turnPage(page.totalPage);" title="尾页">尾页</a>
                    </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>