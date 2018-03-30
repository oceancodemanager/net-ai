
package com.dayee.wintalent.report.resume.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import com.dayee.wintalent.candidates.dao.CandidatesPersonIdFactory;
import com.dayee.wintalent.constant.CandidatesConstants;
import com.dayee.wintalent.constant.Constants;
import com.dayee.wintalent.constant.PropertyConstants;
import com.dayee.wintalent.framework.DaoException;
import com.dayee.wintalent.framework.dao.HbmPageEntityDaoImpl;
import com.dayee.wintalent.framework.dao.jdbc.MappingDao;
import com.dayee.wintalent.framework.dao.jdbc.Sql;
import com.dayee.wintalent.framework.utils.CollectionUtils;
import com.dayee.wintalent.framework.utils.CriterionUtils;
import com.dayee.wintalent.framework.utils.StringUtils;
import com.dayee.wintalent.report.apply.entity.ReportApplyKeyEntity;
import com.dayee.wintalent.report.resume.entity.PersCodeChangeRecord;
import com.dayee.wintalent.report.resume.entity.ReportResume;
import com.dayee.wintalent.report.resume.entity.ReportResumePersEntity;
import com.dayee.wintalent.resume.FindPersCodeException;
import com.dayee.wintalent.resume.ResumeUtils;

public class ReportResumeDaoImpl extends HbmPageEntityDaoImpl
        implements ReportResumeDao {

    private static final Log logger = LogFactory
            .getLog(ReportResumeDaoImpl.class);

    private MappingDao       mappingDao;

    public MappingDao getMappingDao() {

        return mappingDao;
    }

    public void setMappingDao(MappingDao mappingDao) {

        this.mappingDao = mappingDao;
    }

    // private static final String SQL_SUFFIX = "SELECT
    // F_ID,F_PERS_CODE,F_ID_NUM,F_MOBILE_PHONE,F_EMAIL,F_BIRTHDAY FROM
    // T_REPORT_RESUME WHERE ";

    /**
     * * 业务：<br>
     * 为保证同一人有相同的“人员编号
     * "，新简历归档或修改简历归档判重关键字段时，查出本简历所有同一个人的简历，判断是否出现多个人员编号，如果是，则生成新的人员编号，更新所有同一人的简
     * 历 的 人 员 编 号 。 同 时 更 新 所 有 关 联 这 些 归 档 的 简 历 的 人 员 编 号<br>
     * 附加：<br>
     * 记录下人员编号修改的时间等信息 供报表聚合所用
     * 
     * @param oldCodes
     * @param newCode
     * @throws FindPersCodeException
     */

    private void updateResumePerscodeToNewCode(Collection<String> oldCodes,
                                               String newCode,
                                               boolean isTrans,
                                               Session session)
            throws FindPersCodeException {

        if (CollectionUtils.isNotEmpty(oldCodes)
            && StringUtils.hasLength(newCode)) {
            List<String> args = new ArrayList<String>();
            args.add(newCode);
            args.addAll(oldCodes);
            String inSql = CriterionUtils.createInJunction("F_PERS_CODE",
                                                           oldCodes.size());
            String sql = UPDATE_REPORT_RESUME_PERS_CODE + inSql;
            Query query = session.createSQLQuery(sql);
            for (int i = 0; i < args.size(); i++) {
                query.setParameter(i, args.get(i));
            }
            query.executeUpdate();
        }
    }

    private ReportResume addOrFindPersonIdNewTransaction(ReportResume reportResume,
                                                         boolean isAdd) {

        if (reportResume != null) {
            Session session = getHibernateTemplate().getSessionFactory()
                    .openSession();
            session.getTransaction().begin();
            try {
                if (StringUtils.isEmpty(reportResume.getPersCode())) {
                    boolean isTrans = reportResume.isTrans();
                    String idNo = reportResume.getIdNum();
                    String name = reportResume.getName();
                    String gender = reportResume.getGender();
                    Date birthday = reportResume.getBirthday();
                    String phone = reportResume.getMobilePhone();
                    String email = reportResume.getEmail();
                    Set<String> exitsPersCodes = findExitsPersCode(idNo, name,
                                                                   gender,
                                                                   birthday,
                                                                   phone, email,
                                                                   isTrans);
                    String persCode = null;
                    if (exitsPersCodes.isEmpty()) {
                        persCode = CandidatesPersonIdFactory
                                .createPersonIdKey();
                    } else if (exitsPersCodes.size() == 1) {
                        persCode = exitsPersCodes.iterator().next();
                    } else {
                        persCode = CandidatesPersonIdFactory
                                .createPersonIdKey();
                        updateResumePerscodeToNewCode(exitsPersCodes, persCode,
                                                      isTrans, session);
                        reportResume.setExitsPersCodes(exitsPersCodes);
                    }
                    reportResume.setPersCode(persCode);
                }
                if (isAdd) {
                    reportResume.setIsPerson(Constants.YES);
                    reportResume.createKey();
                    session.save(reportResume);
                }
                session.getTransaction().commit();
                if (isAdd) {
                    session.evict(reportResume);
                }
            } catch (Exception e) {
                session.getTransaction().rollback();
                logger.error("addOrFindPersonIdNewTransaction error:"
                             + ExceptionUtils.getStackTrace(e));
                throw new DaoException("新增reportresume异常");
            } finally {
                session.close();
            }
        }
        return reportResume;
    }

    /**
     * 参数说明：<br>
     * isAdd 参数为主动添加一条记录的意思 <br>
     * 业务需求：<br>
     * 1、简历归档信息，新建表，新简历进入系统时同步增加记录<br>
     * 2、使用“是否人员简历”字段标识该简历归档是否为人的简历归档。新简历归档或修改简历归档判重关键字段时，查询本简历归档所有同一个人的简历归档，
     * 取更新日期最新的简历归档设置为0，其他简历归档设置为1。<br>
     * 3、为保证同一人有相同的“人员编号
     * "，新简历归档或修改简历归档判重关键字段时，查出本简历所有同一个人的简历，判断是否出现多个人员编号，如果是，则生成新的人员编号，更新所有同一人的简
     * 历 的 人 员 编 号 。 同 时 更 新 所 有 关 联 这 些 归 档 的 简 历 的 人 员 编 号
     * 
     * @param idNo
     * @param name
     * @param gender
     * @param birthday
     * @param phone
     * @param mail
     * @param isAdd
     * @return
     */
    @Override
    public synchronized ReportResume addOrFindPersonId(String idNo,
                                                       String name,
                                                       String gender,
                                                       Date birthday,
                                                       String phone,
                                                       String email,
                                                       boolean isAdd) {

        ReportResume reportResume = new ReportResume();
        reportResume.setPersonInfo(idNo, name, gender, birthday, phone, email);
        addOrFindPersonIdNewTransaction(reportResume, isAdd);
        Set<String> exitsPersCodes = reportResume.getExitsPersCodes();
        if (exitsPersCodes != null && exitsPersCodes.size() > 1) {
            updateOtherTablePerscodeToNewCode(exitsPersCodes,
                                              reportResume.getPersCode(),
                                              reportResume.isTrans());
        }
        return reportResume;
    }

    /**
     * @see com.dayee.wintalent.report.resume.dao.ReportResumeDaoImpl#addOrFindPersonId(String,
     *      String, String, Date, String, String, boolean)
     * @param reportResume
     * @param isAdd
     * @return
     * @throws FindPersCodeException
     */
    @Override
    public synchronized ReportResume addOrFindPersonId(ReportResume reportResume,
                                                       boolean isAdd) {

        addOrFindPersonIdNewTransaction(reportResume, isAdd);
        Set<String> exitsPersCodes = reportResume.getExitsPersCodes();
        if (exitsPersCodes != null && exitsPersCodes.size() > 1) {
            updateOtherTablePerscodeToNewCode(exitsPersCodes,
                                              reportResume.getPersCode(),
                                              reportResume.isTrans());
        }
        return reportResume;
    }

    @Override
    public synchronized String modifyPersonInfo(String idNo,
                                                String name,
                                                String gender,
                                                Date birthday,
                                                String phone,
                                                String email,
                                                int resumeType,
                                                Integer resumeId,
                                                String oldPersCode) {

        Set<String> exitsPersCodes = findExitsPersCode(idNo, name, gender,
                                                       birthday, phone, email,
                                                       false);
        String persCode = null;
        if (exitsPersCodes.isEmpty()) {
            persCode = CandidatesPersonIdFactory.createPersonIdKey();
        } else if (exitsPersCodes.size() == 1) {
            persCode = exitsPersCodes.iterator().next();
        } else {
            persCode = CandidatesPersonIdFactory.createPersonIdKey();
            updateResumePerscodeToNewCode(exitsPersCodes, persCode, false,
                                          getCurrentSession());
            updateOtherTablePerscodeToNewCode(exitsPersCodes, persCode, false);
        }
        ReportResume resume = null;
        if (resumeType == CandidatesConstants.RESUME_TYPE_APPLY) {
            resume = findReportResumeByCandidateResumeId(resumeId);
        } else if (resumeType == CandidatesConstants.RESUME_TYPE_CAND) {
            resume = findReportResumeByTalentResumeId(resumeId);
        }
        if (resume != null) {
            resume.setPersCode(persCode);
            resume.setPersonInfo(idNo, name, gender, birthday, phone, email);
            super.modifyEntity(resume);
            if (oldPersCode != null && !oldPersCode.equals(persCode)) {
                addPersCodeChangeRecord(resume.getUniqueKey(), persCode);
                Integer candidateResumeId = resume.getCandidateResumeId();
                Integer talentResumeId = resume.getTalentResumeId();
                updatePersCodeInOtherTables(persCode, resume.getUniqueKey(),
                                            candidateResumeId, talentResumeId);
                updatePerCodeOrDeleteResume(oldPersCode);
            }
        }
        return persCode;
    }

    /**
     * 记录下人员编号修改的时间等信息 供报表聚合所用
     */
    private void addPersCodeChangeRecord(Integer reportResumeId,
                                         String newPersCode) {

        Junction applyJun = Restrictions.conjunction();
        applyJun.add(Restrictions.eq(PropertyConstants.resumeId,
                                     reportResumeId));
        List<ReportApplyKeyEntity> applyList = (List<ReportApplyKeyEntity>) super.findEntities(ReportApplyKeyEntity.class,
                                                                                               applyJun);
        if (CollectionUtils.notEmpty(applyList)) {
            for (ReportApplyKeyEntity entity : applyList) {
                PersCodeChangeRecord record = new PersCodeChangeRecord(entity,
                        newPersCode);
                record.createKey();
                super.addEntity(record);
            }
        }
    }

    private static final String UPDATE_REPORT_APPLY_PERS_CODE_BY_RESUME_ID      = "update t_report_apply set F_PERS_CODE=? where f_resume_id=?";

    private static final String UPDATE_APPLY_RESUME_PERS_CODE_BY_ID             = "update t_apply_resume_basic_info set F_PERS_CODE=? where f_id=?";

    private static final String UPDATE_APPLY_REMARK_PERS_CODE_BY_RESUME_ID      = "UPDATE t_apply_remark_info SET F_PERS_CODE=? WHERE F_APPLY_ID IN (SELECT f_id FROM t_apply_info WHERE F_RESUME_ID = ?)";

    private static final String UPDATE_APPLY_HEAD_REMARK_PERS_CODE_BY_RESUME_ID = "UPDATE t_apply_head_remark_info SET F_PERS_CODE=? WHERE F_APPLY_ID IN (SELECT f_id FROM t_apply_info WHERE F_RESUME_ID = ?)";

    private static final String UPDATE_CAND_RESUME_PERS_CODE_BY_ID              = "update t_cand_resume_basic_info set F_PERS_CODE=? where f_id=?";

    /**
     * 改变除t_report_resume表之外所有相关表中人员ID字段内容
     * 
     * @param persCode
     * @param reportResumeId
     * @param candidateResumeId
     * @param talentResumeId
     */
    private void updatePersCodeInOtherTables(String persCode,
                                             Integer reportResumeId,
                                             Integer candidateResumeId,
                                             Integer talentResumeId) {

        super.updateByQuery(UPDATE_REPORT_APPLY_PERS_CODE_BY_RESUME_ID,
                            persCode, reportResumeId);
        // 修改对应的人才库候选库表数据
        if (candidateResumeId != null) {
            super.updateByQuery(UPDATE_APPLY_RESUME_PERS_CODE_BY_ID, persCode,
                                candidateResumeId);
            super.updateByQuery(UPDATE_APPLY_REMARK_PERS_CODE_BY_RESUME_ID,
                                persCode, candidateResumeId);
            super.updateByQuery(UPDATE_APPLY_HEAD_REMARK_PERS_CODE_BY_RESUME_ID,
                                persCode, candidateResumeId);
        }
        if (talentResumeId != null) {
            super.updateByQuery(UPDATE_CAND_RESUME_PERS_CODE_BY_ID, persCode,
                                talentResumeId);
        }
    }

    private static final String SELECT_EMPTY_RESUME = "select F_PERS_CODE from t_report_resume  where ((F_NAME = '' or F_NAME is null ) and (F_ID_NUM = '' or F_ID_NUM is null ) and (F_GENDER = '' or F_GENDER is null ) and F_BIRTHDAY is null and (F_MOBILE_PHONE = '' or F_MOBILE_PHONE is null ) and (F_EMAIL = '' or F_EMAIL is null ) ) limit 1 ";

    private Set<String> findExitsPersCode(String idNo,
                                          String name,
                                          String gender,
                                          Date birthday,
                                          String phone,
                                          String email,
                                          boolean isTrans)
            throws FindPersCodeException {

        boolean persInfoIsEmpty = ResumeUtils
                .persInfoIsEmpty(idNo, name, gender, birthday, phone, email);
        Set<String> persCodeSet = new HashSet<String>();
        if (persInfoIsEmpty) {
            Map<String, String> map = mappingDao
                    .queryForMap(SELECT_EMPTY_RESUME);
            if (map != null) {
                persCodeSet.add(map.get("F_PERS_CODE"));
            }
        } else {
            List<Map<String, Object>> allRepeatList = ResumeUtils
                    .findAllReportRepeatResume(idNo, name, gender, birthday,
                                               phone, email);
            if (CollectionUtils.notEmpty(allRepeatList)) {
                for (Map<String, Object> map : allRepeatList) {
                    String pCode = (String) map.get(Sql.F_PERS_CODE);
                    persCodeSet.add(pCode);
                }
            }
        }
        return persCodeSet;
    }

    private static final String UPDATE_REPORT_RESUME_PERS_CODE     = "update t_report_resume set F_PERS_CODE=? where ";

    private static final String UPDATE_APPLY_RESUME_PERS_CODE      = "update t_apply_resume_basic_info set F_PERS_CODE=? where ";

    private static final String UPDATE_CAND_RESUME_PERS_CODE       = "update t_cand_resume_basic_info set F_PERS_CODE=? where ";

    private static final String UPDATE_APPLY_REMARK_PERS_CODE      = "update t_apply_remark_info set F_PERS_CODE=? where ";

    private static final String UPDATE_APPLY_HEAD_REMARK_PERS_CODE = "update t_apply_head_remark_info set F_PERS_CODE=? where ";

    // private static final String UPDATE_CAND_REMARK_PERS_CODE =
    // "update t_cand_remark_info set F_PERS_CODE=? where ";

    // private static final String UPDATE_REPORT_APPLY_PERS_CODE = "update
    // T_REPORT_APPLY set F_PERS_CODE=? where ";

    /**
     * * 业务：<br>
     * 为保证同一人有相同的“人员编号
     * "，新简历归档或修改简历归档判重关键字段时，查出本简历所有同一个人的简历，判断是否出现多个人员编号，如果是，则生成新的人员编号，更新所有同一人的简
     * 历 的 人 员 编 号 。 同 时 更 新 所 有 关 联 这 些 归 档 的 简 历 的 人 员 编 号<br>
     * 附加：<br>
     * 记录下人员编号修改的时间等信息 供报表聚合所用
     * 
     * @param oldCodes
     * @param newCode
     * @throws FindPersCodeException
     */

    private void updateOtherTablePerscodeToNewCode(Collection<String> oldCodes,
                                                   String newCode,
                                                   boolean isTrans)
            throws FindPersCodeException {

        if (CollectionUtils.isNotEmpty(oldCodes)
            && StringUtils.hasLength(newCode)) {

            List<String> args = new ArrayList<String>();
            args.add(newCode);
            args.addAll(oldCodes);
            String inSql = CriterionUtils.createInJunction("F_PERS_CODE",
                                                           oldCodes.size());
            String sql = UPDATE_APPLY_RESUME_PERS_CODE + inSql;
            mappingDao.update(sql, args);
            sql = UPDATE_CAND_RESUME_PERS_CODE + inSql;
            mappingDao.update(sql, args);
            sql = UPDATE_APPLY_REMARK_PERS_CODE + inSql;
            mappingDao.update(sql, args);
            sql = UPDATE_APPLY_HEAD_REMARK_PERS_CODE + inSql;
            mappingDao.update(sql, args);
            if (!isTrans) {
                // 记录下人员编号修改的时间等信息 供报表聚合所用，同时修改t_report_apply表记录
                Junction junction = Restrictions.conjunction();
                junction.add(CriterionUtils.createInJunction(
                                                             PropertyConstants.persCode,
                                                             oldCodes));
                List<ReportApplyKeyEntity> applyList = super.findEntitys(ReportApplyKeyEntity.class,
                                                                         junction);
                if (CollectionUtils.notEmpty(applyList)) {
                    for (ReportApplyKeyEntity apply : applyList) {
                        PersCodeChangeRecord record = new PersCodeChangeRecord(
                                apply, newCode);
                        record.createKey();
                        super.addEntity(record);
                        // 修改t_report_apply表记录
                        apply.setPersCode(newCode);
                        super.modifyEntity(apply);
                    }
                }
                // sql = UPDATE_REPORT_APPLY_PERS_CODE + inSql;
                // mappingDao.update(sql, args);
            }
        }
    }
    //
    // private static final String UPDATE_IS_PERSON_1 =
    // "update t_report_resume set F_IS_PERSON=? where ";
    //
    // private static final String UPDATE_RESUME_CERT =
    // "update t_report_resume_cert set F_IS_PERSON=? where ";
    //
    // private static final String UPDATE_RESUME_DYNAMIC =
    // "update t_report_resume_dynamic set F_IS_PERSON=? where ";
    //
    // private static final String UPDATE_RESUME_EDU =
    // "update t_report_resume_edu set F_IS_PERSON=? where ";
    //
    // private static final String T_REPORT_RESUME_LANGUAGE =
    // "update t_report_resume_language set F_IS_PERSON=? where ";
    //
    // private static final String T_REPORT_RESUME_WORK =
    // "update t_report_resume_work set F_IS_PERSON=? where ";

    private static final String UPDATE_RESUME_IS_PERSON = "update t_report_resume set F_IS_PERSON=? where F_PERS_CODE=? ";

    private void addReportResume(ReportResume reportResume)
            throws FindPersCodeException {

        String persCode = reportResume.getPersCode();
        if (!reportResume.isEmpty() && StringUtils.hasLength(persCode)) {
            // 可以优化
            // 如果是迁移则其他表的IS_PERSON不做修改,迁移之后用sql修改更快
            // if (!reportResume.isTrans()) {
            // mappingDao.update(UPDATE_RESUME_IS_PERSON, Constants.NO,
            // persCode);
            // }
            // } else {
            // ReportResume example = new ReportResume();
            // example.setPersCode(persCode);
            // List<ReportResume> existList = super.findEntitys(example);
            // if (CollectionUtils.isNotEmpty(existList)) {
            // List<Integer> resumeIds = new ArrayList<Integer>();
            // for (ReportResume resume : existList) {
            // Integer resumeId = resume.getUniqueKey();
            // resumeIds.add(resumeId);
            // }
            // String inSql = CriterionUtils
            // .createInJunction(Sql.F_ID, resumeIds.size());
            // resumeIds.add(0, Constants.NO);
            // Object[] args = resumeIds.toArray();
            // mappingDao.update(UPDATE_IS_PERSON_1 + inSql, args);
            // inSql = inSql.replace(Sql.F_ID, Sql.F_RESUME_ID);
            // mappingDao.update(UPDATE_RESUME_CERT + inSql, args);
            // mappingDao.update(UPDATE_RESUME_DYNAMIC + inSql, args);
            // mappingDao.update(UPDATE_RESUME_EDU + inSql, args);
            // mappingDao.update(T_REPORT_RESUME_LANGUAGE + inSql, args);
            // mappingDao.update(T_REPORT_RESUME_WORK + inSql, args);
            // }
            // }
            reportResume.setIsPerson(Constants.YES);
        }
        reportResume.createKey();
        super.addEntity(reportResume);
        getHibernateTemplate().evict(reportResume);
    }

    @Override
    public ReportResume findReportResumeByCandidateResumeId(Integer applyResumeId) {

        Session session = getHibernateTemplate().getSessionFactory()
                .openSession();
        try {
            Criteria criteria = session.createCriteria(ReportResume.class)
                    .add(Restrictions.eq("candidateResumeId", applyResumeId));
            Object resume = criteria.uniqueResult();
            if (resume != null) {
                session.evict(resume);
                return (ReportResume) resume;
            }
        } catch (Exception e) {
            logger.error("findReportResumeByCandidateResumeId error:"
                         + ExceptionUtils.getStackTrace(e));
            throw new DaoException("根据candidateResumeId查询ReportResume出现异常");
        } finally {
            session.close();
        }
        return null;

    }

    @Override
    public ReportResume findReportResumeByTalentResumeId(Integer talentResumeId) {

        Session session = getHibernateTemplate().getSessionFactory()
                .openSession();
        try {
            Criteria criteria = session.createCriteria(ReportResume.class)
                    .add(Restrictions.eq("talentResumeId", talentResumeId));
            Object resume = criteria.uniqueResult();
            if (resume != null) {
                session.evict(resume);
                return (ReportResume) resume;
            }
        } catch (Exception e) {
            logger.error("findReportResumeByTalentResumeId error:"
                         + ExceptionUtils.getStackTrace(e));
            throw new DaoException("根据talentResumeId查询ReportResume出现异常");
        } finally {
            session.close();
        }
        return null;
    }

    private static final String UPDATE_RESUME_SOURCE = "update t_report_resume set F_RESUME_SOURCE =?, F_CHANNEL_DIC_ID =? , F_NET_CHANNEL_ID =?,F_HEADHUNTING_ID=?,F_UPDATE_RESUME_SOURCE_DATE=? where ";

    private static final String UPDATE_APPLY_SOURCE  = "update t_report_apply set F_RESUME_SOURCE =?, F_CHANNEL_DIC_ID =? , F_NET_CHANNEL_ID =?,F_HEADHUNTING_ID=?,F_UPDATE_RESUME_SOURCE_DATE=? where  ";

    /**
     * 修改简历来源
     */
    @Override
    public void updateResumeSource(Integer resumeScource,
                                   Integer channelDicId,
                                   Integer netChannelId,
                                   Integer headhuntingId,
                                   List<Integer> resumeIdList,
                                   List<Integer> applyIdList,
                                   int resumeType) {

        if (CollectionUtils.notEmpty(resumeIdList)) {
            List<Object> args = new ArrayList<Object>();
            String sql = UPDATE_RESUME_SOURCE;
            if (resumeType == CandidatesConstants.RESUME_TYPE_APPLY) {
                sql = sql + CriterionUtils
                        .createInJunction(Sql.F_CANDIDATE_RESUME_ID,
                                          resumeIdList.size());
            } else if (resumeType == CandidatesConstants.RESUME_TYPE_CAND) {
                sql = sql
                      + CriterionUtils.createInJunction(Sql.F_TALENT_RESUME_ID,
                                                        resumeIdList.size());
            }
            args.add(resumeScource);
            args.add(channelDicId);
            args.add(netChannelId);
            args.add(headhuntingId);
            args.add(new Date());
            args.addAll(resumeIdList);
            mappingDao.update(sql, args.toArray());
        }
        if (CollectionUtils.notEmpty(applyIdList)) {
            List<Object> args = new ArrayList<Object>();
            String sql = UPDATE_APPLY_SOURCE;
            sql = sql + CriterionUtils.createInJunction(Sql.F_ID,
                                                        applyIdList.size());
            args.add(resumeScource);
            args.add(channelDicId);
            args.add(netChannelId);
            args.add(headhuntingId);
            args.add(new Date());
            args.addAll(applyIdList);
            int num = mappingDao.update(sql, args.toArray());
            logger.debug("num :" + num);
        }
    }

    private static final String UPDATE_RESUME_SCORE = "update t_report_resume set F_RESUME_SCORE=? where ";

    /**
     * 修改简历打分
     * 
     * @param resumeScore
     * @param resumeIdList
     */
    @Override
    public void updateResumeScore(Double resumeScore,
                                  List<Integer> resumeIdList,
                                  int resumeType) {

        if (CollectionUtils.notEmpty(resumeIdList)) {
            String sql = UPDATE_RESUME_SCORE;
            if (resumeType == CandidatesConstants.RESUME_TYPE_APPLY) {
                sql = sql + CriterionUtils
                        .createInJunction(Sql.F_CANDIDATE_RESUME_ID,
                                          resumeIdList.size());
            } else if (resumeType == CandidatesConstants.RESUME_TYPE_CAND) {
                sql = sql
                      + CriterionUtils.createInJunction(Sql.F_TALENT_RESUME_ID,
                                                        resumeIdList.size());
            }
            Session session = getHibernateTemplate().getSessionFactory()
                    .openSession();
            session.getTransaction().begin();
            try {
                Query query = session.createSQLQuery(sql);
                query.setParameter(0, resumeScore);
                for (int i = 1; i <= resumeIdList.size(); i++) {
                    query.setParameter(i, resumeIdList.get(i - 1));
                }
                query.executeUpdate();
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                logger.error("updateResumeScore error:"
                             + ExceptionUtils.getStackTrace(e));
                throw new DaoException("更新e报表简历打分异常");
            } finally {
                session.close();
            }
        }
    }

    /**
     * persCode改变后涉及到其他相同人员ID的简可能也要重新生成人员ID
     * 
     * @param oldPersCode
     */
    private void updatePerCodeOrDeleteResume(String oldPersCode) {

        ReportResumePersEntity example = new ReportResumePersEntity();
        example.setPersCode(oldPersCode);
        List<ReportResumePersEntity> list = super.findEntityByExample(example);
        if (CollectionUtils.notEmpty(list)) {
            for (ReportResumePersEntity entity : list) {
                String entityIdNum = entity.getIdNum();
                String entityName = entity.getName();
                String entityGender = entity.getGender();
                Date entityBirthday = entity.getBirthday();
                String entityPhone = entity.getMobilePhone();
                String entityEmail = entity.getEmail();
                boolean persInfoIsEmpty = ResumeUtils
                        .persInfoIsEmpty(entityIdNum, entityName, entityGender,
                                         entityBirthday, entityPhone,
                                         entityEmail);
                if (!persInfoIsEmpty) {
                    List<Map<String, Object>> allList = ResumeUtils
                            .findAllReportRepeatResume(entityIdNum, entityName,
                                                       entityGender,
                                                       entityBirthday,
                                                       entityPhone,
                                                       entityEmail);
                    boolean haveSame = false;
                    if (CollectionUtils.notEmpty(allList)) {
                        for (Map<String, Object> map : allList) {
                            Integer id = (Integer) map.get(Sql.F_RESUME_ID);
                            if (!id.equals(entity.getUniqueKey())) {
                                haveSame = true;
                                break;
                            }
                        }
                    }
                    if (!haveSame) {
                        String personId = CandidatesPersonIdFactory
                                .createPersonIdKey();
                        entity.setPersCode(personId);
                        Integer candidateResumeId = entity
                                .getCandidateResumeId();
                        Integer talentResumeId = entity.getTalentResumeId();
                        logger.debug("更新到：personId:" + personId
                                     + " reportResumeId："
                                     + entity.getUniqueKey()
                                     + " candidateResumeId:"
                                     + candidateResumeId
                                     + " talentResumeId:"
                                     + talentResumeId);
                        super.modifyEntity(entity);
                        addPersCodeChangeRecord(entity.getUniqueKey(),
                                                personId);
                        updatePersCodeInOtherTables(personId,
                                                    entity.getUniqueKey(),
                                                    candidateResumeId,
                                                    talentResumeId);
                    }
                }
            }
        }
    }
}